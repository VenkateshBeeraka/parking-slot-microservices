package com.parking.common.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.parking.common.config.MultiDateDeserializer;

@Entity
public class Availability {

	@Id
	@GeneratedValue
	private Integer id;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonDeserialize(using = MultiDateDeserializer.class)
	private Date fromDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonDeserialize(using = MultiDateDeserializer.class)
	private Date toDate;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = SlotBooking.class)
	@JoinColumn(name = "availabiltyId", referencedColumnName = "id")
	private List<SlotBooking> bookings;

	public Availability() {
		super();
	}

	public Availability(Integer id, Date fromDate, Date toDate, List<SlotBooking> bookings) {
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

	public List<SlotBooking> getBookings() {
		return bookings;
	}

	public void setBookings(List<SlotBooking> bookings) {
		this.bookings = bookings;
	}

	@Override
	public String toString() {
		return "Availability [id=" + id + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
}
