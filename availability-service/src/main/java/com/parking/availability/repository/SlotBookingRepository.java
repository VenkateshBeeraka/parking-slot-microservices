package com.parking.availability.repository;

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
}
