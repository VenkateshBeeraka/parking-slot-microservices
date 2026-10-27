package com.parking.common.config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class MultiDateDeserializer extends JsonDeserializer<Date> {

	private static final String[] DATE_PATTERNS = new String[] {
		// Offset-aware pattern first: the plain 'T' pattern would otherwise match and ignore the offset
		"yyyy-MM-dd'T'HH:mm:ss.SSSX",
		"yyyy-MM-dd HH:mm:ss",
		"yyyy-MM-dd'T'HH:mm:ss",
		"yyyy-MM-dd'T'HH:mm:ss.SSS",
		"yyyy-MM-dd HH:mm",
		"yyyy-MM-dd'T'HH:mm",
		"yyyy-MM-dd"
	};

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String dateStr = p.getText();
		if (dateStr == null || dateStr.trim().isEmpty()) {
			return null;
		}
		dateStr = dateStr.trim();

		for (String pattern : DATE_PATTERNS) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				sdf.setLenient(false);
				return sdf.parse(dateStr);
			} catch (ParseException ignored) {
			}
		}

		try {
			long epoch = Long.parseLong(dateStr);
			return new Date(epoch);
		} catch (NumberFormatException ignored) {
		}

		throw new IllegalArgumentException("Unable to parse date: '" + dateStr 
				+ "'. Supported patterns include: 'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd'T'HH:mm:ss', 'yyyy-MM-dd'.");
	}
}
