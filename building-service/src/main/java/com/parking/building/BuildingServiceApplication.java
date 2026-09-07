package com.parking.building;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.parking.common", "com.parking.building"})
@EntityScan(basePackages = "com.parking.common.entity")
@EnableJpaRepositories(basePackages = {"com.parking.building.repository", "com.parking.common.repository"})
public class BuildingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildingServiceApplication.class, args);
	}
}
