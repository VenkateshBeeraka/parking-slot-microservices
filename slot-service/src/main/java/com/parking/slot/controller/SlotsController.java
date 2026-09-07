package com.parking.slot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.parking.common.constant.RestUri;
import com.parking.common.dto.SlotDTO;
import com.parking.slot.service.SlotService;

@RestController
public class SlotsController {

	@Autowired
	private SlotService slotService;

	@PreAuthorize("hasAuthority('admin')")
	@PostMapping(path = RestUri.SLOT)
	public ResponseEntity<String> slotAdd(@PathVariable String buildingNumber, @RequestBody SlotDTO dto) {
		slotService.addSlot(buildingNumber, dto);
		return ResponseEntity.status(HttpStatus.CREATED).body("Slots added to the Building number " + buildingNumber);
	}

	@PreAuthorize("hasAuthority('admin')")
	@GetMapping(path = RestUri.SLOTS)
	public ResponseEntity<List<SlotDTO>> findAllSlots(@PathVariable String buildingNumber) {
		List<SlotDTO> slots = slotService.getAllSlots(buildingNumber);
		return ResponseEntity.status(HttpStatus.OK).body(slots);
	}

	@PreAuthorize("hasAuthority('admin')")
	@PutMapping(path = RestUri.SLOTS_CHANGES)
	public ResponseEntity<String> updateSlot(@PathVariable String buildingNumber, @PathVariable String slotNumber,
			@RequestBody SlotDTO dto) {
		slotService.updateSlot(buildingNumber, slotNumber, dto);
		return ResponseEntity.status(HttpStatus.OK).body("Slot updated");
	}

	@PreAuthorize("hasAuthority('admin')")
	@DeleteMapping(path = RestUri.SLOTS_CHANGES)
	public ResponseEntity<String> deleteSlot(@PathVariable String buildingNumber, @PathVariable String slotNumber) {
		slotService.removeSlots(buildingNumber, slotNumber);
		return ResponseEntity.status(HttpStatus.OK).body("Slot Removed with SlotNumber " + slotNumber);
	}
}
