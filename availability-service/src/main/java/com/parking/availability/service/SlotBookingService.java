package com.parking.availability.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.parking.availability.repository.AuthenticationRepository;
import com.parking.availability.repository.AvailabilityRepository;
import com.parking.availability.repository.BuildingRepository;
import com.parking.availability.repository.SlotBookingRepository;
import com.parking.common.dto.BookingNotificationEvent;
import com.parking.common.entity.Availability;
import com.parking.common.entity.Building;
import com.parking.common.entity.Slot;
import com.parking.common.entity.SlotBooking;
import com.parking.common.entity.User;
import com.parking.common.exception.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Service
public class SlotBookingService {

	private static final Logger log = LoggerFactory.getLogger(SlotBookingService.class);
	private static final String BOOKING_TOPIC = "parking.booking-notifications";

	@Autowired
	private AuthenticationRepository authenticationRepository;
	@Autowired
	private BuildingRepository buildingRepository;
	@Autowired
	private AvailabilityRepository availabilityRepository;
	@Autowired
	private SlotBookingRepository slotBookingRepository;
	@Autowired(required = false)
	private KafkaTemplate<String, BookingNotificationEvent> kafkaTemplate;

	public int idheader() {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Integer userId = (Integer) token.getPrincipal();
		return userId;
	}

	public String getRoleHeader() {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Collection<GrantedAuthority> role = token.getAuthorities();
		for (GrantedAuthority r : role) {
			if (r.getAuthority().equalsIgnoreCase("admin"))
				return "admin";
		}
		return "user";
	}

	// Bookings without an explicit end last one hour; no single booking may exceed a week
	public static final long DEFAULT_DURATION_MS = 60L * 60 * 1000;
	public static final long MAX_DURATION_MS = 7L * 24 * 60 * 60 * 1000;

	// synchronized so the overlap check and the insert cannot interleave within this instance
	public synchronized String bookSlot(String buildNumber, String slotNo, Date bookingdate, Date endDate) {
		if (bookingdate == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A start date and time is required");
		}
		if (bookingdate.before(new Date())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The start time must be in the future");
		}
		Date end = endDate != null ? endDate : new Date(bookingdate.getTime() + DEFAULT_DURATION_MS);
		if (!end.after(bookingdate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The end time must be after the start time");
		}
		if (end.getTime() - bookingdate.getTime() > MAX_DURATION_MS) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A booking can be at most 7 days long");
		}

		Integer userId = idheader();
		for (Building build : buildingRepository.findAll()) {
			if (!build.getBuildingNumber().equals(buildNumber)) {
				continue;
			}
			if (build.getSlots() == null || build.getSlots().isEmpty()) {
				throw new ResourceNotFoundException("No slots are present in this building");
			}
			for (Slot slot : build.getSlots()) {
				if (!slot.getSlotNumber().equals(slotNo)) {
					continue;
				}
				Availability avail = slot.getAvailability();
				if (avail == null) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "This slot is not open for booking");
				}
				// The whole stay must fall inside the slot's bookable window
				if (bookingdate.before(avail.getFromDate()) || end.after(avail.getToDate())) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "This slot is not bookable for the whole of that time");
				}
				if (slotBookingRepository.countOverlapping(slot.getId(), bookingdate, end) > 0) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "This slot is already booked for part of that time");
				}

				User user = authenticationRepository.findById(userId).orElse(null);
				SlotBooking slotbook = new SlotBooking();
				slotbook.setBookingDate(bookingdate);
				slotbook.setEndDate(end);
				slotbook.setUser(user);
				slotbook.setSlot(slot);
				slotbook = slotBookingRepository.save(slotbook);

				if (avail.getBookings() == null) {
					avail.setBookings(new ArrayList<>());
				}
				avail.getBookings().add(slotbook);
				availabilityRepository.save(avail);
				sendBookingNotification(user, build, slot, bookingdate, end, slotbook.getId());
				return "Booking Sucessfull";
			}
		}
		throw new ResourceNotFoundException("Building Number not valid =" + buildNumber);
	}

	private void sendBookingNotification(User user, Building build, Slot slot, Date bookingDate, Date endDate, Integer bookingId) {
		try {
			if (kafkaTemplate != null) {
				String userEmail = (user != null && user.getEmail() != null) ? user.getEmail() : "user" + idheader() + "@parking.com";
				String userName = (user != null && user.getName() != null) ? user.getName() : "Customer";
				String buildName = (build != null && build.getBuildingName() != null) ? build.getBuildingName() : "Main Building";
				String buildNum = (build != null && build.getBuildingNumber() != null) ? build.getBuildingNumber() : "";
				String slotNum = (slot != null && slot.getSlotNumber() != null) ? slot.getSlotNumber() : "";

				BookingNotificationEvent event = new BookingNotificationEvent(
						bookingId,
						idheader(),
						userEmail,
						userName,
						buildNum,
						buildName,
						slotNum,
						bookingDate,
						"CONFIRMED"
				);
				event.setEndDate(endDate);

				kafkaTemplate.send(BOOKING_TOPIC, String.valueOf(event.getBookingId()), event);
				log.info("📤 Published BookingNotificationEvent to topic '{}' for user: {}", BOOKING_TOPIC, userEmail);
			}
		} catch (Exception e) {
			log.warn("⚠️ Could not publish booking notification to Kafka: {}", e.getMessage());
		}
	}

	public String cancelSlotBook(Integer id) {
		Integer userId = idheader();
		SlotBooking booking = slotBookingRepository.findById(id).orElse(null);
		Date now = new Date();
		if (booking != null) {
			if (booking.getUser() != null && booking.getUser().getId().equals(userId)) {
				if (now.before(booking.getBookingDate())) {
					slotBookingRepository.delete(booking);
					return "Booking Cancelled";
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This reservation has already started and can no longer be cancelled");
				}
			}
		}
		throw new ResourceNotFoundException("enter valid slot booking Id " + id);
	}

	@Transactional
	public int closeExpiredBookings() {
		Date now = new Date();
		List<SlotBooking> expiredBookings = slotBookingRepository.findExpiredBookings(now);
		if (expiredBookings != null && !expiredBookings.isEmpty()) {
			for (SlotBooking booking : expiredBookings) {
				String oldStatus = booking.getStatus();
				booking.setStatus("CLOSED");
				log.info("⏰ Closed slot booking [ID: {}, Slot: {}, Booking Time: {}, Previous Status: {}]",
						booking.getId(),
						(booking.getSlot() != null ? booking.getSlot().getSlotNumber() : "N/A"),
						booking.getBookingDate(),
						oldStatus);
			}
			slotBookingRepository.saveAll(expiredBookings);
			log.info("✅ Marked {} slot booking(s) as CLOSED (end time has passed).", expiredBookings.size());
			return expiredBookings.size();
		}
		return 0;
	}

	public List<Map<String, Object>> getMyBookings() {
		return toBookingDetails(slotBookingRepository.findBookingDetailsByUser(idheader()), false);
	}

	public List<Map<String, Object>> getAllBookingDetails() {
		return toBookingDetails(slotBookingRepository.findAllBookingDetails(), true);
	}

	private List<Map<String, Object>> toBookingDetails(List<Object[]> rows, boolean includeUser) {
		// Same wall-clock format the booking was submitted in
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> result = new ArrayList<>();
		for (Object[] r : rows) {
			Map<String, Object> m = new LinkedHashMap<>();
			m.put("id", r[0]);
			m.put("slotNumber", r[1]);
			m.put("buildingNumber", r[2]);
			m.put("buildingName", r[3]);
			m.put("status", r[4]);
			m.put("bookingDate", r[5] instanceof Date ? fmt.format((Date) r[5]) : r[5]);
			m.put("endDate", r[6] instanceof Date ? fmt.format((Date) r[6]) : r[6]);
			if (includeUser) {
				m.put("userId", r[7]);
				m.put("userName", r[8]);
				m.put("userEmail", r[9]);
			}
			result.add(m);
		}
		return result;
	}

	public long countBookings() {
		return slotBookingRepository.count();
	}

	public List<SlotBooking> getAllBookings() {
		return slotBookingRepository.findAll();
	}
}
