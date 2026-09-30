package com.ridelink.farepayment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ridelink.farepayment.service.FareCalculator;

@Configuration
public class FareConfig {

	@Bean
	FareCalculator fareCalculator() {
		return new FareCalculator();
	}
}
