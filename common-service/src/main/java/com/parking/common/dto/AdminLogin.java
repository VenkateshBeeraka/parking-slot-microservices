package com.parking.common.dto;

import java.util.List;

import com.parking.common.entity.Building;

public class AdminLogin {

	private String jwt;
	private List<Building> listBuildings;

	public AdminLogin() {
		super();
	}

	public AdminLogin(String jwt, List<Building> listBuildings) {
		super();
		this.jwt = jwt;
		this.listBuildings = listBuildings;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public List<Building> getListBuildings() {
		return listBuildings;
	}

	public void setListBuildings(List<Building> listBuildings) {
		this.listBuildings = listBuildings;
	}

	@Override
	public String toString() {
		return "AdminLogin [jwt=" + jwt + ", listBuildings=" + listBuildings + "]";
	}
}
