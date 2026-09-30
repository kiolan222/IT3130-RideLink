package com.ridelink.farepayment.service;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ridelink.farepayment.domain.Payment;
import com.ridelink.farepayment.domain.PaymentStatus;
import com.ridelink.farepayment.dto.EstimateRequest;
import com.ridelink.farepayment.dto.EstimateResponse;
import com.ridelink.farepayment.dto.PaymentRequest;
import com.ridelink.farepayment.dto.PaymentResponse;
import com.ridelink.farepayment.messaging.RideLifecycleEvent;
import com.ridelink.farepayment.repo.PaymentRepository;
import com.ridelink.farepayment.web.ApiException;

@Service
public class FarePaymentService {

	public static final String FARE_RULE = "fare = 150 + 50 * km + 10 * minutes";

	private final PaymentRepository paymentRepository;
	private final FareCalculator fareCalculator;

	public FarePaymentService(PaymentRepository paymentRepository, FareCalculator fareCalculator) {
		this.paymentRepository = paymentRepository;
		this.fareCalculator = fareCalculator;
	}

	public EstimateResponse estimate(EstimateRequest request) {
		FareCalculator.FareBreakdown breakdown = fareCalculator.estimate(request.pickup(), request.destination(),
				request.pickupLat(), request.pickupLng(), request.destLat(), request.destLng());
		return new EstimateResponse(breakdown.distanceKm(), breakdown.estimatedMinutes(), breakdown.amount(),
				breakdown.basis(), FARE_RULE);
	}

	public void onRideCompleted(RideLifecycleEvent event) {
		if (paymentRepository.findByRideId(event.rideId()).isPresent()) {
			return;
		}
		FareCalculator.FareBreakdown breakdown = fareCalculator.estimate(event.pickup(), event.destination(),
				event.pickupLat(), event.pickupLng(), event.destLat(), event.destLng());
		Payment payment = new Payment();
		payment.setRideId(event.rideId());
		payment.setPassengerId(event.passengerId());
		payment.setDriverId(event.driverId());
		payment.setPickup(event.pickup());
		payment.setDestination(event.destination());
		payment.setDistanceKm(breakdown.distanceKm());
		payment.setEstimatedMinutes(breakdown.estimatedMinutes());
		payment.setAmount(breakdown.amount());
		payment.setStatus(PaymentStatus.PENDING);
		payment.setCreatedAt(Instant.now());
		paymentRepository.save(payment);
	}

	public void onRideCancelled(RideLifecycleEvent event) {
		paymentRepository.findByRideId(event.rideId()).ifPresent(payment -> {
			if (payment.getStatus() == PaymentStatus.PENDING) {
				payment.setStatus(PaymentStatus.CANCELLED);
				paymentRepository.save(payment);
			}
		});
	}

	public PaymentResponse charge(String passengerId, PaymentRequest request) {
		Payment payment = paymentRepository.findByRideId(request.rideId())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No pending payment for this ride"));
		if (!passengerId.equals(payment.getPassengerId())) {
			throw new ApiException(HttpStatus.FORBIDDEN, "You can only pay for your own rides");
		}
		if (payment.getStatus() == PaymentStatus.PAID) {
			throw new ApiException(HttpStatus.CONFLICT, "Payment already completed");
		}
		if (payment.getStatus() == PaymentStatus.CANCELLED) {
			throw new ApiException(HttpStatus.CONFLICT, "Ride was cancelled");
		}
		if ("0000".equals(request.cardEnding())) {
			payment.setStatus(PaymentStatus.FAILED);
			payment.setFailureReason("Simulated card declined");
			paymentRepository.save(payment);
			throw new ApiException(HttpStatus.PAYMENT_REQUIRED, "Simulated payment failed for card ending 0000");
		}
		payment.setStatus(PaymentStatus.PAID);
		payment.setPaidAt(Instant.now());
		payment.setFailureReason(null);
		return PaymentResponse.from(paymentRepository.save(payment));
	}

	public PaymentResponse getPayment(String id) {
		return PaymentResponse.from(paymentRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found")));
	}

	public PaymentResponse getByRideId(String rideId) {
		return PaymentResponse.from(paymentRepository.findByRideId(rideId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found for ride")));
	}

	public PaymentResponse getReceipt(String paymentId) {
		return getPayment(paymentId);
	}
}
