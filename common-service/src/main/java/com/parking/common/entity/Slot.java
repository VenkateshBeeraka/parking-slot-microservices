package com.parking.common.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Slot {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer id;

	private String slotNumber;

	private String floornumber = "1";

	private String divisionNo = "1";

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "availabilityId")
	private Availability availability;

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

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	@Override
	public String toString() {
		return "Slot [id=" + id + ", slotNumber=" + slotNumber + ", floornumber=" + floornumber + ", divisionNo="
				+ divisionNo + "]";
	}

	public Slot(Integer id, String slotNumber, String floornumber, String divisionNo, Availability availability) {
		super();
		this.id = id;
		this.slotNumber = slotNumber;
		this.floornumber = floornumber;
		this.divisionNo = divisionNo;
		this.availability = availability;
	}

	public Slot() {
		super();
	}
}
