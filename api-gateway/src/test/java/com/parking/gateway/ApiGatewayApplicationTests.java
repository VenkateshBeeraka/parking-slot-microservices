package com.parking.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import io.swagger.v3.oas.models.OpenAPI;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false"
})
class ApiGatewayApplicationTests {

	@Autowired(required = false)
	private OpenAPI openAPI;

	@Autowired(required = false)
	private RouteLocator routeLocator;

	@Test
	void contextLoads() {
		assertThat(openAPI).isNotNull();
		assertThat(openAPI.getInfo().getTitle()).contains("Parking Slot Microservices - API Gateway");
		assertThat(routeLocator).isNotNull();
	}
}
