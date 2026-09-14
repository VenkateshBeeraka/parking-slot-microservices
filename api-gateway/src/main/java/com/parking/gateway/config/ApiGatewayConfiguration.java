package com.parking.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				// Auth Service routes
				.route("auth-service-login", p -> p.path("/login", "/register")
						.uri("lb://auth-service"))
				.route("auth-service-direct", p -> p.path("/auth-service/**")
						.filters(f -> f.rewritePath("/auth-service/(?<segment>.*)", "/${segment}"))
						.uri("lb://auth-service"))

				// User Service routes
				.route("user-service", p -> p.path("/users/**")
						.uri("lb://user-service"))
				.route("user-service-direct", p -> p.path("/user-service/**")
						.filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/${segment}"))
						.uri("lb://user-service"))

				// Building Service routes
				.route("building-service", p -> p.path("/building/**", "/buildings/**")
						.uri("lb://building-service"))
				.route("building-service-direct", p -> p.path("/building-service/**")
						.filters(f -> f.rewritePath("/building-service/(?<segment>.*)", "/${segment}"))
						.uri("lb://building-service"))

				// Slot Service routes
				.route("slot-service", p -> p.path("/slot/**", "/slots/**")
						.uri("lb://slot-service"))
				.route("slot-service-direct", p -> p.path("/slot-service/**")
						.filters(f -> f.rewritePath("/slot-service/(?<segment>.*)", "/${segment}"))
						.uri("lb://slot-service"))

				// Availability Service routes
				.route("availability-service", p -> p.path("/availability/**", "/booking/**")
						.uri("lb://availability-service"))
				.route("availability-service-direct", p -> p.path("/availability-service/**")
						.filters(f -> f.rewritePath("/availability-service/(?<segment>.*)", "/${segment}"))
						.uri("lb://availability-service"))

				// Admin Service routes
				.route("admin-service", p -> p.path("/admin/**")
						.uri("lb://admin-service"))
				.route("admin-service-direct", p -> p.path("/admin-service/**")
						.filters(f -> f.rewritePath("/admin-service/(?<segment>.*)", "/${segment}"))
						.uri("lb://admin-service"))

				// Notification Service routes
				.route("notification-service", p -> p.path("/notifications/**")
						.uri("lb://notification-service"))
				.route("notification-service-direct", p -> p.path("/notification-service/**")
						.filters(f -> f.rewritePath("/notification-service/(?<segment>.*)", "/${segment}"))
						.uri("lb://notification-service"))
				.build();
	}
}
