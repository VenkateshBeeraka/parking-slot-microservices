package com.parking.notification.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.parking.common.dto.BookingNotificationEvent;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailNotificationService {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

	@Autowired(required = false)
	private JavaMailSender mailSender;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	@Value("${spring.mail.sender-name:Parking Slots}")
	private String senderName;

	// In-memory log of recent dispatched notifications for easy testing and debugging
	private final List<BookingNotificationEvent> recentNotifications = Collections.synchronizedList(new ArrayList<>());

	public void processBookingNotification(BookingNotificationEvent event) {
		recentNotifications.add(0, event);
		if (recentNotifications.size() > 50) {
			recentNotifications.remove(recentNotifications.size() - 1);
		}

		String recipient = event.getRecipientEmail();
		String subject = "✅ Parking Slot Confirmed - Slot " + event.getSlotNumber() + " (" + event.getBuildingName() + ")";
		String formattedDate = formatPeriod(event.getBookingDate(), event.getEndDate());

		String plainTextBody = buildPlainTextEmail(event, formattedDate);
		String htmlBody = buildHtmlEmail(event, formattedDate);

		// If real Gmail SMTP credentials are provided, send the real email
		if (isSmtpConfigured()) {
			try {
				MimeMessage mimeMessage = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				String fromDisplayName = (senderName != null && !senderName.trim().isEmpty())
						? senderName.trim()
						: "Parking Slots";

				helper.setFrom(mailUsername, fromDisplayName);
				helper.setTo(recipient);
				helper.setSubject(subject);
				helper.setText(plainTextBody, htmlBody);

				mailSender.send(mimeMessage);
				log.info("📧 [REAL EMAIL SENT] Dispatched confirmation email to: {} with sender: '{}'", recipient, fromDisplayName);
			} catch (Exception ex) {
				log.warn("⚠️ SMTP dispatch failed (check credentials/network). Falling back to logger: {}", ex.getMessage());
				logFormattedEmail(recipient, subject, plainTextBody);
			}
		} else {
			// Free logger fallback: print formatted email in console
			logFormattedEmail(recipient, subject, plainTextBody);
		}
	}

	// "2026-10-30 10:00 – 13:00", or the full end date when the stay runs past midnight
	private String formatPeriod(Date start, Date end) {
		if (start == null) {
			return "N/A";
		}
		SimpleDateFormat full = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (end == null) {
			return full.format(start);
		}
		SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
		String endText = day.format(start).equals(day.format(end)) ? new SimpleDateFormat("HH:mm").format(end) : full.format(end);
		return full.format(start) + " – " + endText;
	}

	private boolean isSmtpConfigured() {
		return mailSender != null && mailUsername != null && !mailUsername.trim().isEmpty();
	}

	private String buildPlainTextEmail(BookingNotificationEvent event, String formattedDate) {
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

	private String buildHtmlEmail(BookingNotificationEvent event, String formattedDate) {
		String recipientName = escapeHtml(event.getRecipientName() != null ? event.getRecipientName() : "Valued Customer");
		String bookingId = escapeHtml(String.valueOf(event.getBookingId() != null ? event.getBookingId() : "N/A"));
		String buildingName = escapeHtml(event.getBuildingName() != null ? event.getBuildingName() : "Main Building");
		String buildingNumber = escapeHtml(event.getBuildingNumber() != null ? event.getBuildingNumber() : "");
		String slotNumber = escapeHtml(event.getSlotNumber() != null ? event.getSlotNumber() : "N/A");
		String status = escapeHtml(event.getStatus() != null ? event.getStatus() : "CONFIRMED");

		return "<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "  <meta charset=\"UTF-8\">\n"
				+ "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
				+ "  <title>Parking Slot Confirmation</title>\n"
				+ "</head>\n"
				+ "<body style=\"margin:0;padding:0;background-color:#f1f5f9;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;\">\n"
				+ "  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#f1f5f9;padding:36px 12px;\">\n"
				+ "    <tr>\n"
				+ "      <td align=\"center\">\n"
				+ "        <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width:580px;background-color:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 10px 25px rgba(15,23,42,0.08);border:1px solid #e2e8f0;\">\n"
				+ "          <!-- Header -->\n"
				+ "          <tr>\n"
				+ "            <td style=\"background:linear-gradient(135deg, #0f172a 0%, #1e293b 100%);padding:32px 28px;text-align:center;\">\n"
				+ "              <div style=\"display:inline-block;background:#3b82f6;color:#ffffff;font-size:22px;font-weight:bold;width:44px;height:44px;line-height:44px;border-radius:12px;margin-bottom:12px;\">🅿️</div>\n"
				+ "              <h1 style=\"margin:0;color:#ffffff;font-size:22px;font-weight:700;letter-spacing:-0.3px;\">Parking Slots</h1>\n"
				+ "              <p style=\"margin:6px 0 0 0;color:#94a3b8;font-size:13px;letter-spacing:0.8px;text-transform:uppercase;font-weight:600;\">Official Booking Confirmation</p>\n"
				+ "            </td>\n"
				+ "          </tr>\n"
				+ "          <!-- Hero Status -->\n"
				+ "          <tr>\n"
				+ "            <td style=\"padding:28px 28px 12px 28px;text-align:center;\">\n"
				+ "              <span style=\"display:inline-block;background-color:#ecfdf5;color:#059669;border:1px solid #a7f3d0;font-size:12px;font-weight:700;padding:6px 16px;border-radius:9999px;letter-spacing:0.5px;\">✔ BOOKING CONFIRMED</span>\n"
				+ "              <h2 style=\"margin:16px 0 8px 0;color:#0f172a;font-size:20px;font-weight:600;\">Hello " + recipientName + ",</h2>\n"
				+ "              <p style=\"margin:0;color:#64748b;font-size:14px;line-height:1.5;\">Your parking slot has been successfully booked. Please find your reservation receipt below.</p>\n"
				+ "            </td>\n"
				+ "          </tr>\n"
				+ "          <!-- Details Table -->\n"
				+ "          <tr>\n"
				+ "            <td style=\"padding:16px 28px;\">\n"
				+ "              <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#f8fafc;border-radius:12px;border:1px solid #e2e8f0;overflow:hidden;\">\n"
				+ "                <tr>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#64748b;font-size:13px;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;\" width=\"40%\">Booking ID</td>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#0f172a;font-size:15px;font-weight:700;font-family:monospace;\">#" + bookingId + "</td>\n"
				+ "                </tr>\n"
				+ "                <tr>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#64748b;font-size:13px;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;\">Building</td>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#0f172a;font-size:14px;font-weight:600;\">" + buildingName + " <span style=\"color:#64748b;font-size:12px;font-weight:normal;\">(" + buildingNumber + ")</span></td>\n"
				+ "                </tr>\n"
				+ "                <tr>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#64748b;font-size:13px;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;\">Reserved Slot</td>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#2563eb;font-size:16px;font-weight:700;\">" + slotNumber + "</td>\n"
				+ "                </tr>\n"
				+ "                <tr>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#64748b;font-size:13px;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;\">Booking Date</td>\n"
				+ "                  <td style=\"padding:14px 18px;border-bottom:1px solid #e2e8f0;color:#0f172a;font-size:14px;font-weight:600;\">" + formattedDate + "</td>\n"
				+ "                </tr>\n"
				+ "                <tr>\n"
				+ "                  <td style=\"padding:14px 18px;color:#64748b;font-size:13px;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;\">Status</td>\n"
				+ "                  <td style=\"padding:14px 18px;color:#16a34a;font-size:13px;font-weight:700;\">● " + status + "</td>\n"
				+ "                </tr>\n"
				+ "              </table>\n"
				+ "            </td>\n"
				+ "          </tr>\n"
				+ "          <!-- Notice -->\n"
				+ "          <tr>\n"
				+ "            <td style=\"padding:0 28px 20px 28px;\">\n"
				+ "              <div style=\"background-color:#eff6ff;border-left:4px solid #3b82f6;border-radius:6px;padding:12px 16px;color:#1e40af;font-size:13px;line-height:1.5;\">\n"
				+ "                💡 <strong>Gate Entry:</strong> Present your Booking ID <strong>#" + bookingId + "</strong> or this email confirmation at the entrance security gate.\n"
				+ "              </div>\n"
				+ "            </td>\n"
				+ "          </tr>\n"
				+ "          <!-- Footer -->\n"
				+ "          <tr>\n"
				+ "            <td style=\"background-color:#f8fafc;padding:24px 28px;text-align:center;border-top:1px solid #e2e8f0;\">\n"
				+ "              <p style=\"margin:0 0 6px 0;color:#64748b;font-size:13px;\">Need assistance? Reach out to <a href=\"mailto:support@parking.com\" style=\"color:#2563eb;text-decoration:none;font-weight:600;\">support@parking.com</a></p>\n"
				+ "              <p style=\"margin:0;color:#94a3b8;font-size:12px;\">© 2026 Parking Slots Platform. All rights reserved.</p>\n"
				+ "            </td>\n"
				+ "          </tr>\n"
				+ "        </table>\n"
				+ "      </td>\n"
				+ "    </tr>\n"
				+ "  </table>\n"
				+ "</body>\n"
				+ "</html>";
	}

	private String escapeHtml(String input) {
		if (input == null) return "";
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
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
