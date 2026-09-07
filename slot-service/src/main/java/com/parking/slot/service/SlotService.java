package com.parking.slot.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.parking.common.dto.SlotDTO;
import com.parking.common.entity.Building;
import com.parking.common.entity.Slot;
import com.parking.common.entity.User;
import com.parking.common.exception.ResourceNotFoundException;
import com.parking.slot.repository.AuthenticationRepository;
import com.parking.slot.repository.BuildingRepository;
import com.parking.slot.repository.SlotsRepository;

@Service
public class SlotService {

	@Autowired
	private AuthenticationRepository authenticationRepository;
	@Autowired
	private BuildingRepository buildingRepository;
	@Autowired
	private SlotsRepository slotRepository;

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

	public String addSlot(String buildingNumber, SlotDTO dto) {
		Slot slot = new Slot();
		int id = idheader();
		User us = authenticationRepository.findById(id).orElse(null);
		if (us == null) {
			throw new ResourceNotFoundException("No user is present with ID " + id);
		}
		List<Building> li = us.getBuildings();
		if (li != null) {
			for (Building bld : li) {
				if (bld.getBuildingNumber().equals(buildingNumber)) {
					List<Slot> sl = bld.getSlots();
					if (sl == null) {
						sl = new ArrayList<>();
						bld.setSlots(sl);
					}
					slot.setSlotNumber(dto.getSlotNumber());
					slot.setAvailability(dto.getAvailability());
					if (dto.getFloornumber() != null) {
						slot.setFloornumber(dto.getFloornumber());
					}
					if (dto.getDivisionNo() != null) {
						slot.setDivisionNo(dto.getDivisionNo());
					}
					sl.add(slot);
					slotRepository.saveAll(sl);
					buildingRepository.save(bld);
					return "Slot added to building " + buildingNumber;
				}
			}
		}
		throw new ResourceNotFoundException("No Building is present with building Number " + buildingNumber);
	}

	public List<SlotDTO> getAllSlots(String buildingNumber) {
		List<SlotDTO> list = new ArrayList<>();

		User us = authenticationRepository.findById(idheader()).orElse(null);
		if (us == null || us.getBuildings() == null || us.getBuildings().isEmpty()) {
			throw new ResourceNotFoundException("No buildings associated with user");
		}
		List<Building> listofBuildings = us.getBuildings();
		for (Building bld : listofBuildings) {
			if (bld.getBuildingNumber().equals(buildingNumber)) {
				List<Slot> slots = bld.getSlots();
				if (slots != null) {
					for (Slot s : slots) {
						list.add(new SlotDTO(s.getId(), s.getSlotNumber(), s.getFloornumber(), s.getDivisionNo(),
								s.getAvailability()));
					}
				}
				return list;
			}
		}
		throw new ResourceNotFoundException("Invalid Building number " + buildingNumber);
	}

	public String removeSlots(String buildNumber, String slotNumber) {
		User us = authenticationRepository.findById(idheader()).orElse(null);
		if (us != null && us.getBuildings() != null) {
			for (Building bld : us.getBuildings()) {
				if (bld.getBuildingNumber().equals(buildNumber)) {
					List<Slot> ls = bld.getSlots();
					if (ls != null) {
						Iterator<Slot> its = ls.iterator();
						while (its.hasNext()) {
							Slot sl = its.next();
							if (sl.getSlotNumber().equals(slotNumber)) {
								its.remove();
								authenticationRepository.save(us);
								slotRepository.delete(sl);
								return "Slot deleted with building number " + buildNumber;
							}
						}
					}
				}
			}
		}
		throw new ResourceNotFoundException("Building not found with building Number " + buildNumber);
	}

	public String updateSlot(String buildNumber, String slotNumber, SlotDTO dto) {
		User us = authenticationRepository.findById(idheader()).orElse(null);
		if (us != null && us.getBuildings() != null) {
			for (Building bld : us.getBuildings()) {
				if (bld.getBuildingNumber().equals(buildNumber)) {
					List<Slot> ls = bld.getSlots();
					if (ls != null) {
						for (Slot sl : ls) {
							if (sl.getSlotNumber().equals(slotNumber)) {
								if (dto.getSlotNumber() != null) {
									sl.setSlotNumber(dto.getSlotNumber());
								}
								if (dto.getDivisionNo() != null) {
									sl.setDivisionNo(dto.getDivisionNo());
								}
								if (dto.getFloornumber() != null) {
									sl.setFloornumber(dto.getFloornumber());
								}
								slotRepository.save(sl);
								buildingRepository.save(bld);
								return "Slot updated with slotNumber " + slotNumber;
							}
						}
					}
				}
			}
		}
		throw new ResourceNotFoundException("Invalid buildingNumber or slot Id");
	}
}
