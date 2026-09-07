package com.parking.common.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Building {

	@Id
	@GeneratedValue
	private Integer id;
	private String buildingNumber;
	private String buildingName;
	private String area;
	private String town;
	private String state;
	private String landmark;
	private Integer pincode;

	@JsonIgnore
	@OneToMany(targetEntity = Slot.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "buildingId", referencedColumnName = "id")
	private List<Slot> slots;

	public Building() {

	}

	public Building(Integer id, String buildingNumber,
			@Length(min = 8, max = 20, message = "Alteast 8 letters") String buildingName,
			@NotBlank(message = "Area can't be blank") String area,
			@NotBlank(message = "Town can't be blank") String town,
			@NotBlank(message = "Message can't be blank") String state,
			@NotBlank(message = "Landmark can't be blank") String landmark,
			@NotNull(message = "Invalid pincode,Pincode can't be blank") Integer pincode, List<Slot> slots) {
		super();
		this.id = id;
		this.buildingNumber = buildingNumber;
		this.buildingName = buildingName;
		this.area = area;
		this.town = town;
		this.state = state;
		this.landmark = landmark;
		this.pincode = pincode;
		this.slots = slots;
	}

	public List<Slot> getSlots() {
		return slots;
	}

	public void setSlots(List<Slot> slots) {
		this.slots = slots;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getBuildingNumber() {
		return buildingNumber;
	}

	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public Integer getPincode() {
		return pincode;
	}

	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	@Override
	public String toString() {
		return "Building [id=" + id + ", buildingNumber=" + buildingNumber + ", buildingName=" + buildingName
				+ ", area=" + area + ", town=" + town + ", state=" + state + ", landmark=" + landmark + ", pincode="
				+ pincode + "]";
	}
}
