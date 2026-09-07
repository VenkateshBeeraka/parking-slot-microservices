package com.parking.common.dto;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.parking.common.entity.SlotBooking;

public class AvailabilityDTO {

	private Integer id;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date fromDate;

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date toDate;

	private List<SlotBooking> bookings;

	public List<SlotBooking> getBookings() {
		return bookings;
	}

	public void setBookings(List<SlotBooking> bookings) {
		this.bookings = bookings;
	}

	public AvailabilityDTO() {
		super();
	}

	public AvailabilityDTO(Integer id, Date fromDate, Date toDate, List<SlotBooking> bookings) {
		super();
		this.id = id;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.bookings = bookings;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "AvailabilityDTO [id=" + id + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
}
