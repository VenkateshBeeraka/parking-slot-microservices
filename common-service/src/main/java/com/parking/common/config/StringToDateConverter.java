package com.parking.common.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDateConverter implements Converter<String, Date> {

	private static final String[] DATE_PATTERNS = new String[] {
		"yyyy-MM-dd HH:mm:ss",
		"yyyy-MM-dd'T'HH:mm:ss",
		"yyyy-MM-dd'T'HH:mm:ss.SSSX",
		"yyyy-MM-dd'T'HH:mm:ss.SSS",
		"yyyy-MM-dd HH:mm",
		"yyyy-MM-dd'T'HH:mm",
		"yyyy-MM-dd"
	};

	@Override
	public Date convert(String source) {
		if (source == null || source.trim().isEmpty()) {
			return null;
		}

		String cleanSource = source.trim();
		try {
			cleanSource = URLDecoder.decode(cleanSource, StandardCharsets.UTF_8).trim();
		} catch (Exception ignored) {
		}

		for (String pattern : DATE_PATTERNS) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				sdf.setLenient(false);
				return sdf.parse(cleanSource);
			} catch (ParseException ignored) {
			}
		}

		try {
			long epoch = Long.parseLong(cleanSource);
			return new Date(epoch);
		} catch (NumberFormatException ignored) {
		}

		throw new IllegalArgumentException("Cannot convert '" + source 
				+ "' to Date. Supported formats include: 'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd'T'HH:mm:ss', 'yyyy-MM-dd'.");
	}
}
