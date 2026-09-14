package com.parking.notification.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parking.common.dto.BookingNotificationEvent;
import com.parking.notification.service.EmailNotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Controller", description = "Endpoints for viewing and testing email notifications")
public class NotificationController {

	@Autowired
	private EmailNotificationService emailNotificationService;

	@GetMapping("/status")
	@Operation(summary = "Get notification service status")
	public ResponseEntity<Map<String, Object>> getStatus() {
		return ResponseEntity.ok(Map.of(
				"service", "notification-service",
				"status", "UP",
				"kafkaListener", "ACTIVE",
				"totalRecentEvents", emailNotificationService.getRecentNotifications().size()
		));
	}

	@GetMapping("/recent")
	@Operation(summary = "Get list of recent notifications consumed via Kafka")
	public ResponseEntity<List<BookingNotificationEvent>> getRecentNotifications() {
		return ResponseEntity.ok(emailNotificationService.getRecentNotifications());
	}

	@PostMapping("/test")
	@Operation(summary = "Send a manual test notification")
	public ResponseEntity<String> sendTestNotification(@RequestBody(required = false) BookingNotificationEvent testEvent) {
		if (testEvent == null) {
			testEvent = new BookingNotificationEvent(
					999,
					1,
					"test.user@parking.com",
					"Test User",
					"BLD-101",
					"Cyber Towers",
					"A-101",
					new Date(),
					"TEST"
			);
		}
		emailNotificationService.processBookingNotification(testEvent);
		return ResponseEntity.ok("Test notification processed for " + testEvent.getRecipientEmail());
	}
}
