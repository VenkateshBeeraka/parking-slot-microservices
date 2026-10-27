package com.parking.availability.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.parking.common.entity.SlotBooking;
import com.parking.common.repository.Bookings;

@Repository
public interface SlotBookingRepository extends JpaRepository<SlotBooking, Integer> {

	@Query(value = "select b.id as id, s.slot_number as slotNo, bi.building_number as buildingNo, b.status as status, b.booking_date as bookingDate "
			+ "from slot s inner join slot_booking b on s.id=b.slot_id join building bi on bi.id=s.building_id where b.user_id=:id order by b.booking_date", nativeQuery = true)
	List<Bookings> endUserResult(@Param("id") Integer id);

	// End of a booking; bookings stored without an end time are treated as lasting one hour
	String EFFECTIVE_END = "coalesce(b.end_date, b.booking_date + interval '1 hour')";

	// Columns: id, slot_number, building_number, building_name, status, booking_date, end_date, user_id, user_name, user_email
	String BOOKING_DETAILS_SELECT = "select b.id, s.slot_number, bi.building_number, bi.building_name, b.status, b.booking_date, "
			+ EFFECTIVE_END + " as end_date, u.id as user_id, u.name as user_name, u.email as user_email "
			+ "from slot_booking b join slot s on s.id = b.slot_id join building bi on bi.id = s.building_id "
			+ "left join users u on u.id = b.user_id ";

	@Query(value = BOOKING_DETAILS_SELECT + "where b.user_id = :userId order by b.booking_date desc", nativeQuery = true)
	List<Object[]> findBookingDetailsByUser(@Param("userId") Integer userId);

	@Query(value = BOOKING_DETAILS_SELECT + "order by b.booking_date desc", nativeQuery = true)
	List<Object[]> findAllBookingDetails();

	// Bookings on the slot that are still open and overlap [startTime, endTime)
	@Query(value = "select count(*) from slot_booking b where b.slot_id = :slotId and upper(coalesce(b.status, '')) <> 'CLOSED' "
			+ "and b.booking_date < :endTime and " + EFFECTIVE_END + " > :startTime", nativeQuery = true)
	long countOverlapping(@Param("slotId") Integer slotId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

	// Open bookings that are still running or have not started yet
	@Query(value = "select count(*) from slot_booking b where b.slot_id = :slotId and upper(coalesce(b.status, '')) <> 'CLOSED' "
			+ "and " + EFFECTIVE_END + " > :now", nativeQuery = true)
	long countCurrentOrFuture(@Param("slotId") Integer slotId, @Param("now") Date now);

	// A booking is finished once its end time has passed
	@Query(value = "select * from slot_booking b where " + EFFECTIVE_END + " <= :now and upper(coalesce(b.status, '')) <> 'CLOSED'", nativeQuery = true)
	List<SlotBooking> findExpiredBookings(@Param("now") Date now);
}
