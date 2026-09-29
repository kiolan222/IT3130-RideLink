package com.ridelink.ride.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.ridelink.ride.client.EligibleDriver;
import com.ridelink.ride.client.FareEstimate;
import com.ridelink.ride.client.PeerClients;
import com.ridelink.ride.domain.Ride;
import com.ridelink.ride.domain.RideStatus;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.messaging.RideEventPublisher;
import com.ridelink.ride.repo.RideRepository;
import com.ridelink.ride.web.ApiException;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

	@Mock
	private RideRepository rideRepository;
	@Mock
	private PeerClients peerClients;
	@Mock
	private RideEventPublisher eventPublisher;

	private RideService rideService;

	@BeforeEach
	void setUp() {
		rideService = new RideService(rideRepository, peerClients, eventPublisher);
	}

	@Test
	void createFailsWhenNoEligibleDriver() {
		CreateRideRequest request = new CreateRideRequest("Colombo", "Kandy", null, null, null, null);
		when(peerClients.estimateFare(any(), eq("Bearer t"))).thenReturn(new FareEstimate(100, 8, 12, "PLACE_TABLE", "r"));
		when(peerClients.findEligible("Colombo", "Bearer t")).thenReturn(List.of());

		ApiException ex = assertThrows(ApiException.class, () -> rideService.create("p1", "Bearer t", request));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		verify(rideRepository, never()).save(any());
	}

	@Test
	void invalidTransitionIsConflict() {
		Ride ride = new Ride();
		ride.setStatus(RideStatus.REQUESTED);
		ApiException ex = assertThrows(ApiException.class, () -> rideService.transition(ride, RideStatus.COMPLETED));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
	}

	@Test
	void completePublishesEvent() {
		Ride ride = new Ride();
		ride.setId("r1");
		ride.setDriverId("d1");
		ride.setPassengerId("p1");
		ride.setStatus(RideStatus.IN_PROGRESS);
		when(rideRepository.findById("r1")).thenReturn(Optional.of(ride));
		when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		rideService.complete("r1", "d1", "Bearer t");
		verify(eventPublisher).publishCompleted(any());
		verify(peerClients).updateAvailability("d1", "AVAILABLE", "Bearer t");
	}
}
