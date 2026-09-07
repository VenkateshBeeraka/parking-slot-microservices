package com.parking.building.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.parking.building.service.BuildingService;
import com.parking.common.constant.RestUri;
import com.parking.common.dto.BuildingDTO;

@RestController
public class BuildingController {

	@Autowired
	private BuildingService buildingService;

	@PostMapping(path = RestUri.BUILDING)
	@PreAuthorize("hasAuthority('admin')")
	public ResponseEntity<String> addBuilding(@Validated @RequestBody BuildingDTO dto) {
		buildingService.createBuilding(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body("Building Added");
	}

	@GetMapping(path = RestUri.BUILDINGS)
	public ResponseEntity<List<BuildingDTO>> getBuildings() {
		return ResponseEntity.status(HttpStatus.OK).body(buildingService.findbuildings());
	}

	@GetMapping(path = RestUri.BUILDING_PIN)
	public ResponseEntity<List<BuildingDTO>> getBuidingsByPincode(@PathVariable Integer pincode) {
		return ResponseEntity.status(HttpStatus.OK).body(buildingService.getByBuildingPinCode(pincode));
	}

	@GetMapping(path = RestUri.BUILDING_NAME)
	public ResponseEntity<List<BuildingDTO>> getBuidingsByName(@PathVariable String buildingName) {
		return ResponseEntity.status(HttpStatus.OK).body(buildingService.getByBuildingName(buildingName));
	}

	@PreAuthorize("hasAuthority('admin')")
	@PutMapping(path = RestUri.BUILDING_CHANGES)
	public ResponseEntity<String> updateBuilding(@PathVariable String buildingNumber, @RequestBody BuildingDTO dto) {
		buildingService.updateBuildings(buildingNumber, dto);
		return ResponseEntity.status(HttpStatus.OK).body("Building updated");
	}

	@PreAuthorize("hasAuthority('admin')")
	@DeleteMapping(path = RestUri.BUILDING_CHANGES)
	public ResponseEntity<String> deleteBuildingByNo(@PathVariable String buildingNumber) {
		buildingService.deleteBuilding(buildingNumber);
		return ResponseEntity.status(HttpStatus.OK).body("Building Deleted");
	}

	@GetMapping(path = "/buildings/count")
	public ResponseEntity<Long> countBuildings() {
		return ResponseEntity.ok(buildingService.countBuildings());
	}
}
