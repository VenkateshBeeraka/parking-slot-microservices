package com.parking.availability.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parking.availability.repository.SlotBookingRepository;
import com.parking.common.entity.Slot;
import com.parking.common.entity.SlotBooking;

@ExtendWith(MockitoExtension.class)
public class SlotBookingServiceTest {

	@Mock
	private SlotBookingRepository slotBookingRepository;

	@InjectMocks
	private SlotBookingService slotBookingService;

	@Test
	void testCloseExpiredBookings_WhenExpiredExist() {
		Date pastTime = new Date(System.currentTimeMillis() - 3600000); // 1 hour ago
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
		Date pastDate = new Date(System.currentTimeMillis() - 60000); // 1 min ago
		assertThrows(IllegalArgumentException.class, () -> {
			slotBookingService.bookSlot("BLD-101", "A-101", pastDate);
		});
	}
}
