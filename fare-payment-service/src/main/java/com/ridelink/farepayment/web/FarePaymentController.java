package com.ridelink.farepayment.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.farepayment.dto.EstimateRequest;
import com.ridelink.farepayment.dto.EstimateResponse;
import com.ridelink.farepayment.dto.PaymentRequest;
import com.ridelink.farepayment.dto.PaymentResponse;
import com.ridelink.farepayment.security.AuthPrincipal;
import com.ridelink.farepayment.service.FarePaymentService;

import jakarta.validation.Valid;

@RestController
public class FarePaymentController {

	private final FarePaymentService farePaymentService;

	public FarePaymentController(FarePaymentService farePaymentService) {
		this.farePaymentService = farePaymentService;
	}

	@PostMapping("/api/fares/estimate")
	public EstimateResponse estimate(@Valid @RequestBody EstimateRequest request) {
		return farePaymentService.estimate(request);
	}

	@PostMapping("/api/payments")
	@PreAuthorize("hasRole('PASSENGER')")
	public PaymentResponse pay(@AuthenticationPrincipal AuthPrincipal principal,
			@Valid @RequestBody PaymentRequest request) {
		return farePaymentService.charge(principal.getUserId(), request);
	}

	@GetMapping("/api/payments/{id}")
	public PaymentResponse getPayment(@PathVariable String id) {
		return farePaymentService.getPayment(id);
	}

	@GetMapping("/api/payments/ride/{rideId}")
	public PaymentResponse getByRide(@PathVariable String rideId) {
		return farePaymentService.getByRideId(rideId);
	}

	@GetMapping("/api/receipts/{paymentId}")
	public PaymentResponse getReceipt(@PathVariable String paymentId) {
		return farePaymentService.getReceipt(paymentId);
	}
}
