package com.parking.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.parking.common", "com.parking.user"})
@EntityScan(basePackages = "com.parking.common.entity")
@EnableJpaRepositories(basePackages = {"com.parking.user.repository", "com.parking.common.repository"})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
