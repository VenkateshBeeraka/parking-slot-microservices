package com.parking.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parking.admin.service.AdminService;
import com.parking.common.entity.SlotBooking;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('admin')")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/dashboard")
	public ResponseEntity<Map<String, Object>> getDashboard() {
		return ResponseEntity.ok(adminService.getDashboardStats());
	}

	@GetMapping("/bookings")
	public ResponseEntity<List<SlotBooking>> getAllBookings() {
		return ResponseEntity.ok(adminService.getAllBookings());
	}
}
