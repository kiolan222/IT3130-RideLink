package com.ridelink.farepayment.dto;

import jakarta.validation.constraints.NotBlank;

public record EstimateRequest(
		@NotBlank String pickup,
		@NotBlank String destination,
		Double pickupLat,
		Double pickupLng,
		Double destLat,
		Double destLng) {
}
