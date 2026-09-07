package com.parking.common.constant;

public class RestUri {

	// Users & Auth
	public static final String REGISTER = "/register";
	public static final String LOGIN = "/login";

	// Building
	public static final String BUILDINGS = "/buildings";
	public static final String BUILDING = "/building";
	public static final String BUILDING_PIN = "/buildings/{pincode}";
	public static final String BUILDING_NAME = "/buildings/search/{buildingName}";
	public static final String BUILDING_CHANGES = "/building/{buildingNumber}";

	// Slot
	public static final String SLOT = "/slot/{buildingNumber}";
	public static final String SLOTS = "/slots/{buildingNumber}";
	public static final String SLOTS_CHANGES = "/slot/{buildingNumber}/{slotNumber}";

	// Availability
	public static final String AVAILABILITY_CHANGES = "/availability/{buildingNumber}/{slotNumber}";
	public static final String AVAILABILITY = "/availability/{buildNumber}/{date}";

	// Slot Booking
	public static final String BOOKING = "/booking/{buildingNumber}/{slotNumber}/{date}";
	public static final String CANCEL_BOOKING = "/booking/{id}";
}
