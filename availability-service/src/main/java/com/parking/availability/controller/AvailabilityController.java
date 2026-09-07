package com.parking.availability.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.parking.availability.service.AvailabilityService;
import com.parking.common.constant.RestUri;
import com.parking.common.dto.AvailabilityDTO;

@RestController
public class AvailabilityController {

	@Autowired
	private AvailabilityService availabilityService;

	@PreAuthorize("hasAuthority('admin')")
	@PostMapping(path = RestUri.AVAILABILITY_CHANGES)
	public ResponseEntity<String> addAvailability(@Validated @PathVariable String buildingNumber,
			@PathVariable String slotNumber, @RequestBody AvailabilityDTO dto) {
		availabilityService.addAvailability(buildingNumber, slotNumber, dto);
		return ResponseEntity.status(HttpStatus.OK)
				.body("avaliability added to buildingNumber " + buildingNumber + " Slot Number " + slotNumber);
	}

	@PreAuthorize("hasAuthority('admin')")
	@DeleteMapping(path = RestUri.AVAILABILITY_CHANGES)
	public ResponseEntity<String> removeAvailability(@PathVariable String buildingNumber,
			@PathVariable String slotNumber) {
		availabilityService.deleteAvailability(buildingNumber, slotNumber);
		return ResponseEntity.status(HttpStatus.OK).body("Removed avaliability for slot");
	}

	@GetMapping(path = RestUri.AVAILABILITY)
	public ResponseEntity<List<String>> getAvailability(@PathVariable String buildNumber,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		return ResponseEntity.status(HttpStatus.OK).body(availabilityService.findAvailability(buildNumber, date));
	}
}
