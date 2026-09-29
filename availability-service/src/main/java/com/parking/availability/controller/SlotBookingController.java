package com.parking.availability.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parking.availability.service.SlotBookingService;
import com.parking.common.constant.RestUri;
import com.parking.common.entity.SlotBooking;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class SlotBookingController {

	@Autowired
	private SlotBookingService slotBookingService;

	@PreAuthorize("hasAnyAuthority('user', 'admin')")
	@PostMapping(path = {RestUri.BOOKING, "/booking/{buildingNumber}/{slotNumber}"})
	public ResponseEntity<String> bookSlot(
			@PathVariable String buildingNumber,
			@PathVariable String slotNumber,
			@PathVariable(required = false) Date date,
			@RequestParam(name = "date", required = false) Date dateParam) {
		Date bookingDate = (date != null) ? date : dateParam;
		if (bookingDate == null) {
			throw new IllegalArgumentException("Booking date and time must be provided (e.g., 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-ddTHH:mm:ss')");
		}
		return ResponseEntity.status(HttpStatus.OK).body(slotBookingService.bookSlot(buildingNumber, slotNumber, bookingDate));
	}

	@PreAuthorize("hasAuthority('admin')")
	@PostMapping(path = "/booking/close-expired")
	public ResponseEntity<String> closeExpiredBookings() {
		int count = slotBookingService.closeExpiredBookings();
		return ResponseEntity.ok("Closed " + count + " expired booking(s)");
	}

	@DeleteMapping(path = RestUri.CANCEL_BOOKING)
	public ResponseEntity<String> cancelBooking(@PathVariable Integer id) {
		slotBookingService.cancelSlotBook(id);
		return ResponseEntity.status(HttpStatus.OK).body("Booking Cancelled with slot_bookingId " + id);
	}

	@GetMapping(path = "/booking/count")
	public ResponseEntity<Long> countBookings() {
		return ResponseEntity.ok(slotBookingService.countBookings());
	}

	@GetMapping(path = {"/booking", "/booking/all"})
	public ResponseEntity<List<SlotBooking>> getAllBookings() {
		return ResponseEntity.ok(slotBookingService.getAllBookings());
	}
}
