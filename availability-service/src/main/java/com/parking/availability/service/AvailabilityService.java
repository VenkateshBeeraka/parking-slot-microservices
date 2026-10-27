package com.parking.availability.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.parking.availability.repository.AuthenticationRepository;
import com.parking.availability.repository.AvailabilityRepository;
import com.parking.availability.repository.BuildingRepository;
import com.parking.availability.repository.SlotBookingRepository;
import com.parking.availability.repository.SlotsRepository;
import com.parking.common.dto.AvailabilityDTO;
import com.parking.common.entity.Availability;
import com.parking.common.entity.Building;
import com.parking.common.entity.Slot;
import com.parking.common.entity.User;
import com.parking.common.exception.ResourceNotFoundException;

@Service
public class AvailabilityService {

	@Autowired
	private AuthenticationRepository authenticationRepository;
	@Autowired
	private BuildingRepository buildingRepository;
	@Autowired
	private SlotsRepository slotRepository;
	@Autowired
	private AvailabilityRepository availabilityRepository;
	@Autowired
	private SlotBookingRepository slotBookingRepository;

	public int idheader() {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Integer userId = (Integer) token.getPrincipal();
		return userId;
	}

	public String getRoleHeader() {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Collection<GrantedAuthority> role = token.getAuthorities();
		for (GrantedAuthority r : role) {
			if (r.getAuthority().equalsIgnoreCase("admin"))
				return "admin";
		}
		return "user";
	}

	public String addAvailability(String buildNumber, String slotNo, AvailabilityDTO dto) {
		if (dto.getFromDate() == null || dto.getToDate() == null || !dto.getToDate().after(dto.getFromDate())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The window must have a start and an end after it");
		}
		User user = authenticationRepository.findById(idheader()).orElse(null);
		if (user == null || user.getBuildings() == null) {
			throw new ResourceNotFoundException("No buildings found for user");
		}

		for (Building buil : user.getBuildings()) {
			if (buil.getBuildingNumber().equals(buildNumber) && buil.getSlots() != null) {
				for (Slot sl : buil.getSlots()) {
					if (sl.getSlotNumber().equals(slotNo)) {
						// Update the existing window in place so its bookings stay attached and intact;
						// bookings are never taken from the request
						Availability ava = sl.getAvailability() != null ? sl.getAvailability() : new Availability();
						ava.setFromDate(dto.getFromDate());
						ava.setToDate(dto.getToDate());
						if (ava.getBookings() == null) {
							ava.setBookings(new ArrayList<>());
						}
						sl.setAvailability(ava);
						slotRepository.save(sl);
						return "avaliability added to buildingNumber " + buildNumber + " Slot Number " + slotNo;
					}
				}
			}
		}
		throw new ResourceNotFoundException("Invalid SlotNumber or Building Number");
	}

	// Slot numbers in the building that are bookable for the whole of [start, end)
	public List<String> findAvailability(String buildingNumber, Date start, Date end) {
		List<String> result = new ArrayList<>();
		for (Building building : buildingRepository.findAll()) {
			if (!building.getBuildingNumber().equalsIgnoreCase(buildingNumber)) {
				continue;
			}
			if (building.getSlots() != null) {
				for (Slot s : building.getSlots()) {
					Availability availability = s.getAvailability();
					if (availability == null || start.before(availability.getFromDate()) || end.after(availability.getToDate())) {
						continue;
					}
					if (slotBookingRepository.countOverlapping(s.getId(), start, end) == 0) {
						result.add(s.getSlotNumber());
					}
				}
			}
			return result;
		}
		throw new ResourceNotFoundException("No Building found with number " + buildingNumber);
	}

	@Transactional
	public String deleteAvailability(String buildNumber, String slotNo) {
		Integer userId = idheader();
		User us = authenticationRepository.findById(userId).orElse(null);

		if (us == null || us.getBuildings() == null || us.getBuildings().isEmpty()) {
			throw new ResourceNotFoundException("No buildings available");
		}
		for (Building bld : us.getBuildings()) {
			if (bld.getBuildingNumber().equals(buildNumber)) {
				if (bld.getSlots() == null || bld.getSlots().isEmpty())
					throw new ResourceNotFoundException("No slots available");
				for (Slot slo : bld.getSlots()) {
					if (slo.getSlotNumber().equals(slotNo)) {
						Availability avail = slo.getAvailability();
						if (avail == null) {
							return "Deleted Availabilty";
						}
						if (slotBookingRepository.countCurrentOrFuture(slo.getId(), new Date()) > 0) {
							throw new ResponseStatusException(HttpStatus.CONFLICT,
									"This slot has current or upcoming reservations, so its window can't be removed");
						}
						// Detach past bookings first so they are kept as history rather than cascade-deleted
						if (avail.getBookings() != null) {
							avail.getBookings().clear();
						}
						slo.setAvailability(null);
						slotRepository.save(slo);
						availabilityRepository.delete(avail);
						return "Deleted Availabilty";
					}
				}
				throw new ResourceNotFoundException("Invalid slot number");
			}
		}
		throw new ResourceNotFoundException("Invalid building number");
	}
}
