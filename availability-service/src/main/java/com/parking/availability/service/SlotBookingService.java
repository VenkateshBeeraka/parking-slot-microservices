package com.parking.availability.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

	public String bookSlot(String buildNumber, String slotNo, Date bookingdate) {
		Integer userId = idheader();
		boolean booking = true;
		List<Building> listB = buildingRepository.findAll();

		for (Building build : listB) {
			if (build.getBuildingNumber().equals(buildNumber)) {
				if (build.getSlots() == null || build.getSlots().isEmpty()) {
					throw new ResourceNotFoundException("No slots are present in this building");
				}
				for (Slot slot : build.getSlots()) {
					if (slot.getSlotNumber().equals(slotNo)) {
						if (slot.getAvailability() == null) {
							throw new ResourceNotFoundException("no availability for this slot");
						}
						Availability avail = slot.getAvailability();

						if (bookingdate.after(avail.getFromDate()) && bookingdate.before(avail.getToDate())) {
							List<SlotBooking> abook = avail.getBookings();
							if (abook == null) {
								abook = new ArrayList<>();
								avail.setBookings(abook);
							}

							if (abook.isEmpty()) {
								User user = authenticationRepository.findById(userId).orElse(null);
								SlotBooking slotbook = new SlotBooking();
								slotbook.setBookingDate(bookingdate);
								slotbook.setUser(user);
								slotbook.setSlot(slot);
								slotbook = slotBookingRepository.save(slotbook);

								abook.add(slotbook);
								avail.setBookings(abook);
								availabilityRepository.save(avail);
								sendBookingNotification(user, build, slot, bookingdate, slotbook.getId());
								return "Booking Sucessfull";
							} else {
								for (SlotBooking sb : abook) {
									if (sb.getBookingDate().equals(bookingdate)) {
										booking = false;
										break;
									}
								}
								if (booking) {
									User user = authenticationRepository.findById(userId).orElse(null);
									SlotBooking slotbook = new SlotBooking();
									slotbook.setBookingDate(bookingdate);
									slotbook.setUser(user);
									slotbook.setSlot(slot);
									slotbook = slotBookingRepository.save(slotbook);

									abook.add(slotbook);
									avail.setBookings(abook);
									availabilityRepository.save(avail);
									sendBookingNotification(user, build, slot, bookingdate, slotbook.getId());
									return "Booking Sucessfull";
								} else {
									throw new ResourceNotFoundException("Already booked");
								}
							}
						} else {
							throw new ResourceNotFoundException("not available on the date " + bookingdate);
						}
					}
				}
			}
		}
		throw new ResourceNotFoundException("Building Number not valid =" + buildNumber);
	}

	private void sendBookingNotification(User user, Building build, Slot slot, Date bookingDate, Integer bookingId) {
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
		Date date = new Date();
		if (booking != null) {
			if (booking.getUser() != null && booking.getUser().getId().equals(userId)) {
				if (date.compareTo(booking.getBookingDate()) < 0) {
					slotBookingRepository.delete(booking);
					return "Booking Cancelled";
				}
			}
		}
		throw new ResourceNotFoundException("enter valid slot booking Id " + id);
	}

	public long countBookings() {
		return slotBookingRepository.count();
	}

	public List<SlotBooking> getAllBookings() {
		return slotBookingRepository.findAll();
	}
}
