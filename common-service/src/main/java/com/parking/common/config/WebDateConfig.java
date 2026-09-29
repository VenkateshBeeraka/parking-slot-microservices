package com.parking.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebDateConfig implements WebMvcConfigurer {

	@Autowired
	private StringToDateConverter stringToDateConverter;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(stringToDateConverter);
	}
}
