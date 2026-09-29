package com.ridelink.farepayment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
		@NotBlank String rideId,
		@NotBlank @Pattern(regexp = "\\d{4}", message = "cardEnding must be 4 digits") String cardEnding) {
}
