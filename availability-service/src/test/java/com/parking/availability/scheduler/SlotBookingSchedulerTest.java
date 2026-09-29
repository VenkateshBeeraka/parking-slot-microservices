package com.parking.availability.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parking.availability.service.SlotBookingService;

@ExtendWith(MockitoExtension.class)
public class SlotBookingSchedulerTest {

	@Mock
	private SlotBookingService slotBookingService;

	@InjectMocks
	private SlotBookingScheduler slotBookingScheduler;

	@Test
	void testScheduleCloseExpiredBookings_CallsService() {
		when(slotBookingService.closeExpiredBookings()).thenReturn(3);

		slotBookingScheduler.scheduleCloseExpiredBookings();

		verify(slotBookingService, times(1)).closeExpiredBookings();
	}
}
