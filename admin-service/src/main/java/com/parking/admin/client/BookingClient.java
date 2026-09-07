package com.parking.admin.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.parking.common.entity.SlotBooking;

@FeignClient(name = "availability-service")
public interface BookingClient {

	@GetMapping("/booking/count")
	Long countBookings();

	@GetMapping("/booking")
	List<SlotBooking> getAllBookings();
}
