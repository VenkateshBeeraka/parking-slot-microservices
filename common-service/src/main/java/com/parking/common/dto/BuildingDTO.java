package com.parking.common.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.parking.common.entity.Slot;

public class BuildingDTO {

	private Integer id;
	@NotBlank(message = "building number can't be blank")
	private String buildingNumber;
	@Length(min = 8, max = 20, message = "Alteast 8 letters")
	private String buildingName;
	@NotBlank(message = "Area can't be blank")
	private String area;
	@NotBlank(message = "Town can't be blank")
	private String town;
	@NotBlank(message = "Message can't be blank")
	private String state;
	@NotBlank(message = "Landmark can't be blank")
	private String landmark;
	@NotNull(message = "Invalid pincode,Pincode can't be Null")
	private Integer pincode;
	private List<Slot> slots;

	public List<Slot> getSlots() {
		return slots;
	}

	public void setSlots(List<Slot> slots) {
		this.slots = slots;
	}

	public BuildingDTO() {
		super();
	}

	public BuildingDTO(Integer id, @NotBlank(message = "building number can't be blank") String buildingNumber,
			@Length(min = 8, max = 20, message = "Alteast 8 letters") String buildingName,
			@NotBlank(message = "Area can't be blank") String area,
			@NotBlank(message = "Town can't be blank") String town,
			@NotBlank(message = "Message can't be blank") String state,
			@NotBlank(message = "Landmark can't be blank") String landmark,
			@NotNull(message = "Invalid pincode,Pincode can't be Null") Integer pincode) {
		super();
		this.id = id;
		this.buildingNumber = buildingNumber;
		this.buildingName = buildingName;
		this.area = area;
		this.town = town;
		this.state = state;
		this.landmark = landmark;
		this.pincode = pincode;
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
