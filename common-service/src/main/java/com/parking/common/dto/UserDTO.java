package com.parking.common.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.parking.common.entity.Building;

public class UserDTO {

	private Integer id;
	@NotBlank
	private String name;
	@Email
	private String email;
	@NotBlank
	@Pattern(regexp = "^(?=.*\\d)(?=.*[A-Za-z])[A-Za-z0-9]{8,20}$", message = "Passwords should have at least 8 characters and alphanumeric")
	private String password;
	@NotBlank
	private String role;
	@NotBlank
	private String city;
	private List<Building> buildings;

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public UserDTO(Integer id, @NotBlank String name, @Email String email,
			@NotBlank @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Za-z])[A-Za-z0-9]{8,20}$", message = "Passwords should have at least 8 characters and alphanumeric") String password,
			@NotBlank String role, @NotBlank String city) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.city = city;
	}

	public UserDTO() {
		super();
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", city=" + city + "]";
	}
}
