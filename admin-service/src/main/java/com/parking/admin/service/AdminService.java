package com.parking.admin.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parking.admin.client.BookingClient;
import com.parking.admin.client.BuildingClient;
import com.parking.admin.client.UserClient;
import com.parking.common.entity.SlotBooking;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

	@Autowired
	private UserClient userClient;

	@Autowired
	private BuildingClient buildingClient;

	@Autowired
	private BookingClient bookingClient;

	@CircuitBreaker(name = "adminStatsService", fallbackMethod = "getDashboardStatsFallback")
	@Retry(name = "adminStatsService", fallbackMethod = "getDashboardStatsFallback")
	public Map<String, Object> getDashboardStats() {
		logger.info("Fetching dashboard stats via OpenFeign from user, building, and availability microservices...");
		Map<String, Object> stats = new HashMap<>();
		stats.put("totalUsers", userClient.countUsers());
		stats.put("totalBuildings", buildingClient.countBuildings());
		stats.put("totalBookings", bookingClient.countBookings());
		stats.put("status", "UP");
		return stats;
	}

	public Map<String, Object> getDashboardStatsFallback(Throwable throwable) {
		logger.warn("Circuit breaker or retry fallback triggered for getDashboardStats. Error: {}", throwable.getMessage());
		Map<String, Object> fallbackStats = new HashMap<>();
		fallbackStats.put("totalUsers", -1L);
		fallbackStats.put("totalBuildings", -1L);
		fallbackStats.put("totalBookings", -1L);
		fallbackStats.put("status", "DEGRADED - Downstream microservice(s) unavailable");
		fallbackStats.put("error", throwable.getMessage());
		return fallbackStats;
	}

	@CircuitBreaker(name = "adminBookingsService", fallbackMethod = "getAllBookingsFallback")
	public List<SlotBooking> getAllBookings() {
		logger.info("Fetching all bookings via OpenFeign from availability-service...");
		return bookingClient.getAllBookings();
	}

	public List<SlotBooking> getAllBookingsFallback(Throwable throwable) {
		logger.warn("Fallback for getAllBookings triggered. Error: {}", throwable.getMessage());
		return Collections.emptyList();
	}
}
