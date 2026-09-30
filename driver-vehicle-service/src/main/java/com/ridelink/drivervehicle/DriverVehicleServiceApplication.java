package com.ridelink.drivervehicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ridelink.drivervehicle.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class DriverVehicleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriverVehicleServiceApplication.class, args);
	}

}
