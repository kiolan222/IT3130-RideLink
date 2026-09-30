package com.ridelink.ride.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info().title("RideLink Ride Management Service").version("1.0")
						.description("Ride requests, driver assignment and lifecycle transitions."))
				.components(new Components().addSecuritySchemes("bearer-jwt",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
	}
}
