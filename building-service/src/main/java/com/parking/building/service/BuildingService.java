package com.parking.building.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.parking.building.repository.AuthenticationRepository;
import com.parking.building.repository.BuildingRepository;
import com.parking.common.dto.BuildingDTO;
import com.parking.common.entity.Building;
import com.parking.common.entity.User;
import com.parking.common.exception.ResourceNotFoundException;

@Service
public class BuildingService {

	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Autowired
	private BuildingRepository buildingRepository;

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

	public void createBuilding(BuildingDTO dto) {
		Integer userId = idheader();
		User us = authenticationRepository.findById(userId).orElse(null);
		if (us != null) {
			List<Building> buildings = buildingRepository.findAll();
			Iterator<Building> itrb = buildings.iterator();
			while (itrb.hasNext()) {
				Building bld = itrb.next();
				if (bld.getBuildingNumber().equals(dto.getBuildingNumber())) {
					throw new ResourceNotFoundException("building number cannot be duplicate");
				}
			}
			Building build = new Building();
			build.setBuildingNumber(dto.getBuildingNumber());
			build.setBuildingName(dto.getBuildingName());
			build.setArea(dto.getArea());
			build.setTown(dto.getTown());
			build.setState(dto.getState());
			build.setLandmark(dto.getLandmark());
			build.setPincode(dto.getPincode());
			build.setSlots(dto.getSlots());
			if (us.getBuildings() == null) {
				us.setBuildings(new ArrayList<>());
			}
			us.getBuildings().add(build);
			authenticationRepository.save(us);
		} else {
			throw new ResourceNotFoundException("User not found with ID " + userId);
		}
	}

	public List<BuildingDTO> findbuildings() {
		List<BuildingDTO> list = new ArrayList<>();
		if (getRoleHeader().equals("admin")) {
			User user = authenticationRepository.findById(idheader()).orElse(null);
			if (user == null || user.getBuildings() == null || user.getBuildings().isEmpty()) {
				throw new ResourceNotFoundException("No Buildings Associated to user");
			} else {
				List<Building> buildings = user.getBuildings();
				for (Building build : buildings) {
					list.add(new BuildingDTO(build.getId(), build.getBuildingNumber(), build.getBuildingName(),
							build.getArea(), build.getTown(), build.getState(), build.getLandmark(),
							build.getPincode()));
				}
				return list;
			}
		} else {
			List<Building> bld = buildingRepository.findAll();
			if (bld.isEmpty()) {
				throw new ResourceNotFoundException("No buildings Available");
			} else {
				for (Building build : bld) {
					list.add(new BuildingDTO(build.getId(), build.getBuildingNumber(), build.getBuildingName(),
							build.getArea(), build.getTown(), build.getState(), build.getLandmark(),
							build.getPincode()));
				}
				return list;
			}
		}
	}

	public List<BuildingDTO> getByBuildingPinCode(Integer pin) {
		ArrayList<BuildingDTO> listofbuildings = new ArrayList<>();

		if (getRoleHeader().equals("admin")) {
			User us = authenticationRepository.findById(idheader()).orElse(null);
			if (us != null && us.getBuildings() != null) {
				List<Building> lb = us.getBuildings();
				for (Building build : lb) {
					if (build.getPincode().equals(pin)) {
						listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
								build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
								build.getLandmark(), build.getPincode()));
					}
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid pincode this " + pin + " is not valid");
			}
			return listofbuildings;
		} else {
			List<Building> listb = buildingRepository.findAll();
			for (Building build : listb) {
				if (build.getPincode().equals(pin)) {
					listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
							build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
							build.getLandmark(), build.getPincode()));
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid pincode this " + pin + " is not valid");
			}
			return listofbuildings;
		}
	}

	public List<BuildingDTO> getByBuildingName(String name) {
		ArrayList<BuildingDTO> listofbuildings = new ArrayList<>();

		if (getRoleHeader().equals("admin")) {
			User us = authenticationRepository.findById(idheader()).orElse(null);
			if (us != null && us.getBuildings() != null) {
				List<Building> lb = us.getBuildings();
				for (Building build : lb) {
					if (build.getBuildingName().equalsIgnoreCase(name)) {
						listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
								build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
								build.getLandmark(), build.getPincode()));
					}
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid BuildingName this " + name + " is not valid");
			}
			return listofbuildings;
		} else {
			List<Building> listB = buildingRepository.findAll();
			for (Building build : listB) {
				if (build.getBuildingName().equalsIgnoreCase(name)) {
					listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
							build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
							build.getLandmark(), build.getPincode()));
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid BuildingName this " + name + " is not valid");
			}
			return listofbuildings;
		}
	}

	public String deleteBuilding(String buildingNumber) {
		User us = authenticationRepository.findById(idheader()).orElse(null);
		if (us != null && us.getBuildings() != null) {
			List<Building> li = us.getBuildings();
			Iterator<Building> itr = li.iterator();

			while (itr.hasNext()) {
				Building bld = itr.next();
				if (bld.getBuildingNumber().equals(buildingNumber)) {
					itr.remove();
					authenticationRepository.save(us);
					buildingRepository.delete(bld);
					return "Building deleted with building number " + buildingNumber;
				}
			}
		}
		throw new ResourceNotFoundException("No Building Found with Building Number " + buildingNumber);
	}

	public String updateBuildings(String buildingNumber, BuildingDTO dto) {
		Integer userId = idheader();
		User us = authenticationRepository.findById(userId).orElse(null);

		if (us != null && us.getBuildings() != null) {
			List<Building> lb = us.getBuildings();
			for (Building building : lb) {
				if (building.getBuildingNumber().equals(buildingNumber)) {
					if (dto.getArea() != null) building.setArea(dto.getArea());
					if (dto.getBuildingName() != null) building.setBuildingName(dto.getBuildingName());
					if (dto.getLandmark() != null) building.setLandmark(dto.getLandmark());
					if (dto.getPincode() != null) building.setPincode(dto.getPincode());
					if (dto.getState() != null) building.setState(dto.getState());
					if (dto.getTown() != null) building.setTown(dto.getTown());

					authenticationRepository.save(us);
					return "Sucessfully updated";
				}
			}
		}
		throw new ResourceNotFoundException("Invalid Building Number");
	}

	public long countBuildings() {
		return buildingRepository.count();
	}
}
