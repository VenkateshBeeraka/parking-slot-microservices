package com.parking.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

	@Bean
	public OpenAPI gatewayOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Parking Slot Microservices - API Gateway")
						.version("1.0.0")
						.description("Central API Gateway entry point aggregating all microservices: "
								+ "Auth, User, Building, Slot, Availability, Admin, and Notification Services. "
								+ "Use the definition selector at the top right to explore individual microservice APIs.")
						.contact(new Contact()
								.name("Parking Slot System Engineering")
								.email("support@parkingslot.com"))
						.license(new License().name("Apache 2.0")))
				.addServersItem(new Server().url("/").description("API Gateway Default Server"))
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
				.components(new Components()
						.addSecuritySchemes(SECURITY_SCHEME_NAME,
								new SecurityScheme()
										.name(SECURITY_SCHEME_NAME)
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("Enter your JWT Bearer token generated from Auth Service (/login or /register)")));
	}
}
