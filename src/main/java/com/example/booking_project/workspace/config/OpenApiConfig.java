package com.example.booking_project.workspace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 21, 2023
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components())
				.info(new Info().title("Booking Domain APIs").description("APIs for Booking Domain.")
						.termsOfService("terms").contact(new Contact().email("ISDevTeam@marlabs.com"))
						.license(new License().name("Marlabs")).version("1.0"));
	}
}