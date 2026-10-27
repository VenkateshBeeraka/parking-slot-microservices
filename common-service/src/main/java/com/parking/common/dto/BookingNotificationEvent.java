package com.parking.common.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.parking.common.config.MultiDateDeserializer;

public class BookingNotificationEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer bookingId;
	private Integer userId;
	private String recipientEmail;
	private String recipientName;
	private String buildingNumber;
	private String buildingName;
	private String slotNumber;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonDeserialize(using = MultiDateDeserializer.class)
	private Date bookingDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonDeserialize(using = MultiDateDeserializer.class)
	private Date endDate;

	private String status;

	// An instant, so it is serialized with an explicit UTC offset (bookingDate stays wall-clock)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
	@JsonDeserialize(using = MultiDateDeserializer.class)
	private Date timestamp;

	public BookingNotificationEvent() {
		super();
		this.timestamp = new Date();
	}

	public BookingNotificationEvent(Integer bookingId, Integer userId, String recipientEmail, String recipientName,
			String buildingNumber, String buildingName, String slotNumber, Date bookingDate, String status) {
		super();
		this.bookingId = bookingId;
		this.userId = userId;
		this.recipientEmail = recipientEmail;
		this.recipientName = recipientName;
		this.buildingNumber = buildingNumber;
		this.buildingName = buildingName;
		this.slotNumber = slotNumber;
		this.bookingDate = bookingDate;
		this.status = status;
		this.timestamp = new Date();
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getBuildingNumber() {
		return buildingNumber;
	}

	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(String slotNumber) {
		this.slotNumber = slotNumber;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "BookingNotificationEvent [bookingId=" + bookingId + ", userId=" + userId + ", recipientEmail="
				+ recipientEmail + ", recipientName=" + recipientName + ", buildingNumber=" + buildingNumber
				+ ", buildingName=" + buildingName + ", slotNumber=" + slotNumber + ", bookingDate=" + bookingDate
				+ ", endDate=" + endDate
				+ ", status=" + status + ", timestamp=" + timestamp + "]";
	}
}
