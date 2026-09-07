package com.parking.common.dto;

import java.util.List;

import com.parking.common.repository.Bookings;

public class UserLogin {

	private String jwt;
	private List<Bookings> listSlotBookings;

	public UserLogin() {
		super();
	}

	public UserLogin(String jwt, List<Bookings> listSlotBookings) {
		super();
		this.jwt = jwt;
		this.listSlotBookings = listSlotBookings;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public List<Bookings> getListSlotBookings() {
		return listSlotBookings;
	}

	public void setListSlotBookings(List<Bookings> listSlotBookings) {
		this.listSlotBookings = listSlotBookings;
	}

	@Override
	public String toString() {
		return "UserLogin [jwt=" + jwt + ", listSlotBookings=" + listSlotBookings + "]";
	}

}
