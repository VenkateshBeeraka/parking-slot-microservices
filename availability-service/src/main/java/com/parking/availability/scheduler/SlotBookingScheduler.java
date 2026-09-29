package com.parking.availability.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.parking.availability.service.SlotBookingService;

@Component
public class SlotBookingScheduler {

	private static final Logger log = LoggerFactory.getLogger(SlotBookingScheduler.class);

	@Autowired
	private SlotBookingService slotBookingService;

	/**
	 * Periodically checks for slot bookings whose scheduled time has crossed the present time.
	 * If crossed, updates their status to 'CLOSED'.
	 * Defaults to running every 60 seconds (1 minute), configurable via 'parking.scheduler.booking-status.fixed-rate'.
	 */
	@Scheduled(fixedRateString = "${parking.scheduler.booking-status.fixed-rate:60000}")
	public void scheduleCloseExpiredBookings() {
		log.debug("⏰ [SCHEDULER] Checking for expired slot bookings to close...");
		try {
			int closedCount = slotBookingService.closeExpiredBookings();
			if (closedCount > 0) {
				log.info("⏰ [SCHEDULER SUCCESS] Closed {} expired slot booking(s) whose booking time crossed present time.", closedCount);
			}
		} catch (Exception e) {
			log.error("❌ [SCHEDULER ERROR] Failed to close expired slot bookings: {}", e.getMessage(), e);
		}
	}
}
