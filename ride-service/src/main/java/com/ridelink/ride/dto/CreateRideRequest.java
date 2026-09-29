package com.ridelink.ride.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRideRequest(
		@NotBlank String pickup,
		@NotBlank String destination,
		Double pickupLat,
		Double pickupLng,
		Double destLat,
		Double destLng) {
}
