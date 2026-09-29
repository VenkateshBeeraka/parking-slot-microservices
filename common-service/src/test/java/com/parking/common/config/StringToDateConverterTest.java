package com.parking.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringToDateConverterTest {

	private StringToDateConverter converter;

	@BeforeEach
	void setUp() {
		converter = new StringToDateConverter();
	}

	@Test
	void testNullOrEmpty() {
		assertNull(converter.convert(null));
		assertNull(converter.convert("   "));
	}

	@Test
	void testDateTimeStandardPattern() {
		Date result = converter.convert("2026-09-28 14:30:45");
		assertNotNull(result);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assertEquals("2026-09-28 14:30:45", sdf.format(result));
	}

	@Test
	void testIsoDateTimePattern() {
		Date result = converter.convert("2026-09-28T14:30:45");
		assertNotNull(result);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assertEquals("2026-09-28 14:30:45", sdf.format(result));
	}

	@Test
	void testDateOnlyPattern() {
		Date result = converter.convert("2026-09-28");
		assertNotNull(result);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		assertEquals("2026-09-28", sdf.format(result));
	}

	@Test
	void testUrlEncodedPattern() {
		Date result = converter.convert("2026-09-28%2014:30:45");
		assertNotNull(result);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assertEquals("2026-09-28 14:30:45", sdf.format(result));
	}

	@Test
	void testInvalidPatternThrows() {
		assertThrows(IllegalArgumentException.class, () -> converter.convert("invalid-date-format"));
	}
}
