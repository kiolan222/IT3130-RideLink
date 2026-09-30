package com.ridelink.farepayment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.ridelink.farepayment.domain.Payment;
import com.ridelink.farepayment.domain.PaymentStatus;
import com.ridelink.farepayment.dto.EstimateRequest;
import com.ridelink.farepayment.dto.PaymentRequest;
import com.ridelink.farepayment.repo.PaymentRepository;
import com.ridelink.farepayment.web.ApiException;

@ExtendWith(MockitoExtension.class)
class FarePaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	private FarePaymentService service;

	@BeforeEach
	void setUp() {
		service = new FarePaymentService(paymentRepository, new FareCalculator());
	}

	@Test
	void colomboKandyUsesPlaceTable() {
		var estimate = service.estimate(new EstimateRequest("Colombo", "Kandy", null, null, null, null));
		assertEquals(115.0, estimate.distanceKm());
		assertEquals(150 + 50 * 115 + 10 * 172.5, estimate.amount());
	}

	@Test
	void defaultDistanceWhenUnknownPlaces() {
		var estimate = service.estimate(new EstimateRequest("A", "B", null, null, null, null));
		assertEquals(8.0, estimate.distanceKm());
	}

	@Test
	void cardEnding0000Fails() {
		Payment payment = new Payment();
		payment.setRideId("r1");
		payment.setPassengerId("p1");
		payment.setStatus(PaymentStatus.PENDING);
		when(paymentRepository.findByRideId("r1")).thenReturn(Optional.of(payment));
		when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		ApiException ex = assertThrows(ApiException.class,
				() -> service.charge("p1", new PaymentRequest("r1", "0000")));
		assertEquals(HttpStatus.PAYMENT_REQUIRED, ex.getStatus());
		ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
		verify(paymentRepository).save(captor.capture());
		assertEquals(PaymentStatus.FAILED, captor.getValue().getStatus());
	}

	@Test
	void haversineProducesPositiveDistance() {
		double km = FareCalculator.haversineKm(6.9271, 79.8612, 7.2906, 80.6337);
		assertTrue(km > 90 && km < 130);
	}
}
