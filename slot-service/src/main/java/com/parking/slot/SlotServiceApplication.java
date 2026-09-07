package com.parking.slot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.parking.common", "com.parking.slot"})
@EntityScan(basePackages = "com.parking.common.entity")
@EnableJpaRepositories(basePackages = {"com.parking.slot.repository", "com.parking.common.repository"})
public class SlotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlotServiceApplication.class, args);
	}
}
