package com.parking.availability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.parking.common", "com.parking.availability"})
@EntityScan(basePackages = "com.parking.common.entity")
@EnableJpaRepositories(basePackages = {"com.parking.availability.repository", "com.parking.common.repository"})
public class AvailabilityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvailabilityServiceApplication.class, args);
	}
}
