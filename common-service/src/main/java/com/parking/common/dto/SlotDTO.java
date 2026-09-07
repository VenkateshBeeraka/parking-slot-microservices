package com.parking.common.dto;

import com.parking.common.entity.Availability;

public class SlotDTO {

	private Integer id;
	private String slotNumber;
	private String floornumber = "1";
	private String divisionNo = "1";
	private Availability availability;

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public SlotDTO() {
		super();
	}

	public SlotDTO(Integer id, String slotNumber, String floornumber, String divisionNo, Availability availability) {
		super();
		this.id = id;
		this.slotNumber = slotNumber;
		this.floornumber = floornumber;
		this.divisionNo = divisionNo;
		this.availability = availability;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(String slotNumber) {
		this.slotNumber = slotNumber;
	}

	public String getFloornumber() {
		return floornumber;
	}

	public void setFloornumber(String floornumber) {
		this.floornumber = floornumber;
	}

	public String getDivisionNo() {
		return divisionNo;
	}

	public void setDivisionNo(String divisionNo) {
		this.divisionNo = divisionNo;
	}

	@Override
	public String toString() {
		return "SlotDTO [id=" + id + ", slotNumber=" + slotNumber + ", floornumber=" + floornumber + ", divisionNo="
				+ divisionNo + "]";
	}
}
