package com.parking.common.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class SlotBooking {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer id;

	private String status = "SLOT BOOKED";

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date bookingDate;

	@JsonIgnore
	@ManyToOne
	private User user;

	@JsonIgnore
	@ManyToOne
	private Slot slot;

	public SlotBooking() {
		super();
	}

	public SlotBooking(Integer id, String status, Date bookingDate, User user, Slot slot) {
		super();
		this.id = id;
		this.status = status;
		this.bookingDate = bookingDate;
		this.user = user;
		this.slot = slot;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Slot getSlot() {
		return slot;
	}

	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	@Override
	public String toString() {
		return "SlotBooking [id=" + id + ", status=" + status + ", bookingDate=" + bookingDate + ", user=" + user + "]";
	}
}
