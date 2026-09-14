package com.parking.notification.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.parking.common.dto.BookingNotificationEvent;
import com.parking.notification.service.EmailNotificationService;

@Component
public class BookingNotificationListener {

	private static final Logger log = LoggerFactory.getLogger(BookingNotificationListener.class);

	@Autowired
	private EmailNotificationService emailNotificationService;

	@KafkaListener(topics = "parking.booking-notifications", groupId = "notification-group")
	public void consumeBookingNotification(BookingNotificationEvent event) {
		log.info("📥 [KAFKA CONSUMED] Received booking event for Slot: {}, Building: {}, User: {}",
				event.getSlotNumber(), event.getBuildingName(), event.getRecipientEmail());

		emailNotificationService.processBookingNotification(event);
	}
}
