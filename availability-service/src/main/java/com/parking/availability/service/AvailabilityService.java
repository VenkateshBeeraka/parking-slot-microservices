package com.parking.availability.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.parking.availability.repository.AuthenticationRepository;
import com.parking.availability.repository.AvailabilityRepository;
import com.parking.availability.repository.BuildingRepository;
import com.parking.availability.repository.SlotsRepository;
import com.parking.common.dto.AvailabilityDTO;
import com.parking.common.entity.Availability;
import com.parking.common.entity.Building;
import com.parking.common.entity.Slot;
import com.parking.common.entity.SlotBooking;
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
		Availability ava = new Availability();
		User user = authenticationRepository.findById(idheader()).orElse(null);
		if (user == null || user.getBuildings() == null) {
			throw new ResourceNotFoundException("No buildings found for user");
		}

		List<Building> buildingslist = user.getBuildings();
		for (Building buil : buildingslist) {
			if (buil.getBuildingNumber().equals(buildNumber)) {
				List<Slot> slots = buil.getSlots();
				if (slots != null) {
					for (Slot sl : slots) {
						if (sl.getSlotNumber().equals(slotNo)) {
							ava.setFromDate(dto.getFromDate());
							ava.setToDate(dto.getToDate());
							ava.setBookings(dto.getBookings());
							sl.setAvailability(ava);
							slotRepository.save(sl);
							return "avaliability added to buildingNumber " + buildNumber + " Slot Number " + slotNo;
						}
					}
				}
			}
		}
		throw new ResourceNotFoundException("Invalid SlotNumber or Building Number");
	}

	public List<String> findAvailability(String buildingNumber, Date date) {
		String role = getRoleHeader();
		Integer userId = idheader();
		ArrayList<String> slots = new ArrayList<>();

		if (role.equals("admin")) {
			User user = authenticationRepository.findById(userId).orElse(null);
			if (user == null || user.getBuildings() == null || user.getBuildings().isEmpty())
				throw new ResourceNotFoundException("Building are not present for this Admin");

			for (Building building : user.getBuildings()) {
				if (building.getBuildingNumber().equals(buildingNumber)) {
					if (building.getSlots() == null || building.getSlots().isEmpty()) {
						throw new ResourceNotFoundException("No Slots Available for This Building");
					}
					for (Slot slot : building.getSlots()) {
						if (slot.getAvailability() != null) {
							Availability ava = slot.getAvailability();
							if (date.after(ava.getFromDate()) && date.before(ava.getToDate())) {
								if (ava.getBookings() == null || ava.getBookings().isEmpty()) {
									slots.add(slot.getSlotNumber());
								} else {
									boolean available = true;
									for (SlotBooking bk : ava.getBookings()) {
										if (bk.getBookingDate().equals(date)) {
											available = false;
											break;
										}
									}
									if (available) {
										slots.add(slot.getSlotNumber());
									}
								}
							}
						}
					}
					if (slots.isEmpty()) {
						throw new ResourceNotFoundException("No Slots are present in that Building");
					} else {
						return slots;
					}
				}
			}
			throw new ResourceNotFoundException("Invalid Building Id");
		} else {
			List<String> result = new ArrayList<>();
			List<Building> list = buildingRepository.findAll();
			for (Building building : list) {
				if (building.getBuildingNumber().equals(buildingNumber)) {
					List<Slot> listS = building.getSlots();
					if (listS != null) {
						for (Slot s : listS) {
							if (s.getAvailability() != null) {
								Availability availability = s.getAvailability();
								if (date.after(availability.getFromDate()) && date.before(availability.getToDate())) {
									if (availability.getBookings() == null || availability.getBookings().isEmpty()) {
										result.add(s.getSlotNumber());
									} else {
										boolean available = true;
										for (SlotBooking bk : availability.getBookings()) {
											if (bk.getBookingDate().equals(date)) {
												available = false;
												break;
											}
										}
										if (available) {
											result.add(s.getSlotNumber());
										}
									}
								}
							}
						}
					}
					if (result.isEmpty()) {
						throw new ResourceNotFoundException("No Slots are present in that Building for booking");
					} else {
						return result;
					}
				}
			}
			throw new ResourceNotFoundException("No Buildings Available for this user");
		}
	}

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
						if (slo.getAvailability() != null) {
							if (slo.getAvailability().getBookings() == null || slo.getAvailability().getBookings().isEmpty()) {
								Availability avail = slo.getAvailability();
								slo.setAvailability(null);
								slotRepository.save(slo);
								availabilityRepository.delete(avail);
								return "Deleted Availabilty";
							}
						}
					}
				}
				throw new ResourceNotFoundException("Invalid slot number");
			}
		}
		throw new ResourceNotFoundException("Invalid building number");
	}
}
