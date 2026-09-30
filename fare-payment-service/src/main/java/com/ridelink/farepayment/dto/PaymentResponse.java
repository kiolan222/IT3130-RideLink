package com.ridelink.farepayment.dto;

import java.time.Instant;

import com.ridelink.farepayment.domain.Payment;
import com.ridelink.farepayment.domain.PaymentStatus;

public record PaymentResponse(String id, String rideId, String passengerId, String driverId, String pickup,
		String destination, double distanceKm, double estimatedMinutes, double amount, PaymentStatus status,
		Instant createdAt, Instant paidAt, String failureReason) {

	public static PaymentResponse from(Payment payment) {
		return new PaymentResponse(payment.getId(), payment.getRideId(), payment.getPassengerId(), payment.getDriverId(),
				payment.getPickup(), payment.getDestination(), payment.getDistanceKm(), payment.getEstimatedMinutes(),
				payment.getAmount(), payment.getStatus(), payment.getCreatedAt(), payment.getPaidAt(),
				payment.getFailureReason());
	}
}
