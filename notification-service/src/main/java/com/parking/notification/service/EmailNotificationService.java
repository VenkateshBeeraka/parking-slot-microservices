package com.parking.notification.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.parking.common.dto.BookingNotificationEvent;

@Service
public class EmailNotificationService {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

	@Autowired(required = false)
	private JavaMailSender mailSender;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	// In-memory log of recent dispatched notifications for easy testing and debugging
	private final List<BookingNotificationEvent> recentNotifications = Collections.synchronizedList(new ArrayList<>());

	public void processBookingNotification(BookingNotificationEvent event) {
		recentNotifications.add(0, event);
		if (recentNotifications.size() > 50) {
			recentNotifications.remove(recentNotifications.size() - 1);
		}

		String recipient = event.getRecipientEmail();
		String subject = "✅ Parking Slot Confirmed - Slot " + event.getSlotNumber() + " (" + event.getBuildingName() + ")";
		String formattedDate = event.getBookingDate() != null
				? new SimpleDateFormat("yyyy-MM-dd").format(event.getBookingDate())
				: "N/A";

		String emailBody = buildEmailBody(event, formattedDate);

		// If real Gmail SMTP credentials are provided, send the real email
		if (isSmtpConfigured()) {
			try {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(mailUsername);
				message.setTo(recipient);
				message.setSubject(subject);
				message.setText(emailBody);

				mailSender.send(message);
				log.info("📧 [REAL EMAIL SENT] Dispatched confirmation email to: {}", recipient);
			} catch (Exception ex) {
				log.warn("⚠️ SMTP dispatch failed (check credentials/network). Falling back to logger: {}", ex.getMessage());
				logFormattedEmail(recipient, subject, emailBody);
			}
		} else {
			// Free logger fallback: print formatted email in console
			logFormattedEmail(recipient, subject, emailBody);
		}
	}

	private boolean isSmtpConfigured() {
		return mailSender != null && mailUsername != null && !mailUsername.trim().isEmpty();
	}

	private String buildEmailBody(BookingNotificationEvent event, String formattedDate) {
		return "Hello " + (event.getRecipientName() != null ? event.getRecipientName() : "Valued Customer") + ",\n\n"
				+ "Your parking slot booking has been confirmed successfully!\n\n"
				+ "--------------------------------------------------------\n"
				+ " BOOKING DETAILS\n"
				+ "--------------------------------------------------------\n"
				+ " Booking ID:      #" + event.getBookingId() + "\n"
				+ " Building:        " + event.getBuildingName() + " (" + event.getBuildingNumber() + ")\n"
				+ " Slot Number:     " + event.getSlotNumber() + "\n"
				+ " Date of Booking: " + formattedDate + "\n"
				+ " Status:          " + event.getStatus() + "\n"
				+ "--------------------------------------------------------\n\n"
				+ "Thank you for choosing Parking Slot Services!\n"
				+ "Need assistance? Contact support@parking.com";
	}

	private void logFormattedEmail(String recipient, String subject, String body) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n========================= [EMAIL NOTIFICATION DISPATCHED] =========================\n");
		sb.append("TO:      ").append(recipient).append("\n");
		sb.append("SUBJECT: ").append(subject).append("\n");
		sb.append("CONTENT:\n").append(body).append("\n");
		sb.append("(Note: Set SPRING_MAIL_USERNAME and SPRING_MAIL_PASSWORD to send via real Gmail SMTP)\n");
		sb.append("====================================================================================");
		log.info("{}", sb.toString());
	}

	public List<BookingNotificationEvent> getRecentNotifications() {
		return new ArrayList<>(recentNotifications);
	}
}
