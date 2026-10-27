
package com.parking.availability.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.parking.availability.repository.AuthenticationRepository;
import com.parking.availability.repository.AvailabilityRepository;
import com.parking.availability.repository.BuildingRepository;
import com.parking.availability.repository.SlotBookingRepository;
import com.parking.common.entity.Availability;
import com.parking.common.entity.Building;
import com.parking.common.entity.Slot;
import com.parking.common.entity.SlotBooking;

@ExtendWith(MockitoExtension.class)
public class SlotBookingServiceTest {

	private static final long HOUR = 3600000L;

	@Mock
	private SlotBookingRepository slotBookingRepository;

	@Mock
	private BuildingRepository buildingRepository;

	@Mock
	private AuthenticationRepository authenticationRepository;

	@Mock
	private AvailabilityRepository availabilityRepository;

	@InjectMocks
	private SlotBookingService slotBookingService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	private Slot setUpSlotWithWindow(Date from, Date to) {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(5, null, List.of()));

		Availability availability = new Availability();
		availability.setFromDate(from);
		availability.setToDate(to);

		Slot slot = new Slot();
		slot.setId(11);
		slot.setSlotNumber("A-101");
		slot.setAvailability(availability);

		Building building = new Building();
		building.setBuildingNumber("BLD-101");
		building.setSlots(new ArrayList<>(List.of(slot)));

		when(buildingRepository.findAll()).thenReturn(List.of(building));
		return slot;
	}

	private static HttpStatus statusOf(ResponseStatusException e) {
		return HttpStatus.valueOf(e.getStatusCode().value());
	}

	@Test
	void testCloseExpiredBookings_WhenExpiredExist() {
		Date pastTime = new Date(System.currentTimeMillis() - HOUR);
		Slot slot = new Slot();
		slot.setSlotNumber("A-101");

		SlotBooking b1 = new SlotBooking();
		b1.setId(1);
		b1.setStatus("SLOT BOOKED");
		b1.setBookingDate(pastTime);
		b1.setSlot(slot);

		SlotBooking b2 = new SlotBooking();
		b2.setId(2);
		b2.setStatus("CONFIRMED");
		b2.setBookingDate(pastTime);
		b2.setSlot(slot);

		List<SlotBooking> expired = new ArrayList<>();
		expired.add(b1);
		expired.add(b2);

		when(slotBookingRepository.findExpiredBookings(any(Date.class))).thenReturn(expired);

		int count = slotBookingService.closeExpiredBookings();

		assertEquals(2, count);
		assertEquals("CLOSED", b1.getStatus());
		assertEquals("CLOSED", b2.getStatus());
		verify(slotBookingRepository, times(1)).saveAll(expired);
	}

	@Test
	void testCloseExpiredBookings_WhenNoneExpired() {
		when(slotBookingRepository.findExpiredBookings(any(Date.class))).thenReturn(new ArrayList<>());

		int count = slotBookingService.closeExpiredBookings();

		assertEquals(0, count);
		verify(slotBookingRepository, times(0)).saveAll(any());
	}

	@Test
	void testBookSlot_PastDateThrowsException() {
		Date pastDate = new Date(System.currentTimeMillis() - 60000);
		ResponseStatusException e = assertThrows(ResponseStatusException.class,
				() -> slotBookingService.bookSlot("BLD-101", "A-101", pastDate, null));
		assertEquals(HttpStatus.BAD_REQUEST, statusOf(e));
	}

	@Test
	void testBookSlot_EndBeforeStartRejected() {
		Date start = new Date(System.currentTimeMillis() + 2 * HOUR);
		Date end = new Date(start.getTime() - HOUR);
		ResponseStatusException e = assertThrows(ResponseStatusException.class,
				() -> slotBookingService.bookSlot("BLD-101", "A-101", start, end));
		assertEquals(HttpStatus.BAD_REQUEST, statusOf(e));
	}

	@Test
	void testBookSlot_LongerThanAWeekRejected() {
		Date start = new Date(System.currentTimeMillis() + HOUR);
		Date end = new Date(start.getTime() + SlotBookingService.MAX_DURATION_MS + HOUR);
		ResponseStatusException e = assertThrows(ResponseStatusException.class,
				() -> slotBookingService.bookSlot("BLD-101", "A-101", start, end));
		assertEquals(HttpStatus.BAD_REQUEST, statusOf(e));
	}

	@Test
	void testBookSlot_OverlappingBookingRejected() {
		long now = System.currentTimeMillis();
		Slot slot = setUpSlotWithWindow(new Date(now - HOUR), new Date(now + 48 * HOUR));
		Date start = new Date(now + 2 * HOUR);
		Date end = new Date(now + 4 * HOUR);
		when(slotBookingRepository.countOverlapping(slot.getId(), start, end)).thenReturn(1L);

		ResponseStatusException e = assertThrows(ResponseStatusException.class,
				() -> slotBookingService.bookSlot("BLD-101", "A-101", start, end));

		assertEquals(HttpStatus.CONFLICT, statusOf(e));
		verify(slotBookingRepository, never()).save(any());
	}

	@Test
	void testBookSlot_EndOutsideWindowRejected() {
		long now = System.currentTimeMillis();
		setUpSlotWithWindow(new Date(now - HOUR), new Date(now + 3 * HOUR));
		Date start = new Date(now + 2 * HOUR);
		Date end = new Date(now + 5 * HOUR);

		ResponseStatusException e = assertThrows(ResponseStatusException.class,
				() -> slotBookingService.bookSlot("BLD-101", "A-101", start, end));

		assertEquals(HttpStatus.CONFLICT, statusOf(e));
		verify(slotBookingRepository, never()).save(any());
	}

	@Test
	void testBookSlot_SavesStartAndEnd() {
		long now = System.currentTimeMillis();
		Slot slot = setUpSlotWithWindow(new Date(now - HOUR), new Date(now + 48 * HOUR));
		Date start = new Date(now + 2 * HOUR);
		Date end = new Date(now + 5 * HOUR);
		when(slotBookingRepository.countOverlapping(slot.getId(), start, end)).thenReturn(0L);
		when(authenticationRepository.findById(5)).thenReturn(Optional.empty());
		when(slotBookingRepository.save(any(SlotBooking.class))).thenAnswer(inv -> inv.getArgument(0));

		slotBookingService.bookSlot("BLD-101", "A-101", start, end);

		ArgumentCaptor<SlotBooking> saved = ArgumentCaptor.forClass(SlotBooking.class);
		verify(slotBookingRepository).save(saved.capture());
		assertEquals(start, saved.getValue().getBookingDate());
		assertEquals(end, saved.getValue().getEndDate());
		assertEquals(slot, saved.getValue().getSlot());
	}

	@Test
	void testBookSlot_DefaultsToOneHourWithoutEnd() {
		long now = System.currentTimeMillis();
		Slot slot = setUpSlotWithWindow(new Date(now - HOUR), new Date(now + 48 * HOUR));
		Date start = new Date(now + 2 * HOUR);
		Date expectedEnd = new Date(start.getTime() + SlotBookingService.DEFAULT_DURATION_MS);
		when(slotBookingRepository.countOverlapping(eq(slot.getId()), eq(start), eq(expectedEnd))).thenReturn(0L);
		when(authenticationRepository.findById(5)).thenReturn(Optional.empty());
		when(slotBookingRepository.save(any(SlotBooking.class))).thenAnswer(inv -> inv.getArgument(0));

		slotBookingService.bookSlot("BLD-101", "A-101", start, null);

		ArgumentCaptor<SlotBooking> saved = ArgumentCaptor.forClass(SlotBooking.class);
		verify(slotBookingRepository).save(saved.capture());
		assertEquals(expectedEnd, saved.getValue().getEndDate());
	}
}
