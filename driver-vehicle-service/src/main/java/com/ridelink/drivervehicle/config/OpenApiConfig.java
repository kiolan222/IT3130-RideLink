package com.ridelink.drivervehicle.config;

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
				.info(new Info().title("RideLink Driver & Vehicle Service").version("1.0")
						.description("Driver operational profile, vehicle, availability and eligible-driver lookup."))
				.components(new Components().addSecuritySchemes("bearer-jwt",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
	}
}
