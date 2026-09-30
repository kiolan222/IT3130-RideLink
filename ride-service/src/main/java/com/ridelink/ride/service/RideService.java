package com.ridelink.ride.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ridelink.ride.client.EligibleDriver;
import com.ridelink.ride.client.FareEstimate;
import com.ridelink.ride.client.PeerClients;
import com.ridelink.ride.domain.Ride;
import com.ridelink.ride.domain.RideStatus;
import com.ridelink.ride.domain.StatusChange;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.messaging.RideEventPublisher;
import com.ridelink.ride.repo.RideRepository;
import com.ridelink.ride.web.ApiException;

@Service
public class RideService {

	private final RideRepository rideRepository;
	private final PeerClients peerClients;
	private final RideEventPublisher eventPublisher;

	public RideService(RideRepository rideRepository, PeerClients peerClients, RideEventPublisher eventPublisher) {
		this.rideRepository = rideRepository;
		this.peerClients = peerClients;
		this.eventPublisher = eventPublisher;
	}

	public RideResponse create(String passengerId, String bearerToken, CreateRideRequest request) {
		FareEstimate estimate = peerClients.estimateFare(request, bearerToken);
		List<EligibleDriver> drivers = peerClients.findEligible(request.pickup(), bearerToken);
		if (drivers.isEmpty()) {
			throw new ApiException(HttpStatus.CONFLICT, "No available driver in this pickup area");
		}
		EligibleDriver chosen = drivers.get(0);
		Instant now = Instant.now();
		Ride ride = new Ride();
		ride.setPassengerId(passengerId);
		ride.setDriverId(chosen.accountId());
		ride.setPickup(request.pickup());
		ride.setDestination(request.destination());
		ride.setPickupLat(request.pickupLat());
		ride.setPickupLng(request.pickupLng());
		ride.setDestLat(request.destLat());
		ride.setDestLng(request.destLng());
		ride.setEstimatedFare(estimate == null ? null : estimate.amount());
		ride.setCreatedAt(now);
		ride.setUpdatedAt(now);
		ride.setHistory(new ArrayList<>());
		applyStatus(ride, RideStatus.REQUESTED, now);
		applyStatus(ride, RideStatus.ASSIGNED, now);
		Ride saved = rideRepository.save(ride);
		peerClients.updateAvailability(chosen.accountId(), "BUSY", bearerToken);
		return RideResponse.from(saved);
	}

	public RideResponse accept(String rideId, String driverId) {
		Ride ride = requireRide(rideId);
		assertDriver(ride, driverId);
		transition(ride, RideStatus.ACCEPTED);
		return RideResponse.from(rideRepository.save(ride));
	}

	public RideResponse start(String rideId, String driverId) {
		Ride ride = requireRide(rideId);
		assertDriver(ride, driverId);
		transition(ride, RideStatus.IN_PROGRESS);
		return RideResponse.from(rideRepository.save(ride));
	}

	public RideResponse complete(String rideId, String driverId, String bearerToken) {
		Ride ride = requireRide(rideId);
		assertDriver(ride, driverId);
		transition(ride, RideStatus.COMPLETED);
		Ride saved = rideRepository.save(ride);
		releaseDriver(saved, bearerToken);
		eventPublisher.publishCompleted(saved);
		return RideResponse.from(saved);
	}

	public RideResponse cancel(String rideId, String userId, String role, String bearerToken) {
		Ride ride = requireRide(rideId);
		boolean passenger = "PASSENGER".equals(role) && userId.equals(ride.getPassengerId());
		boolean driver = "DRIVER".equals(role) && userId.equals(ride.getDriverId());
		boolean admin = "ADMIN".equals(role);
		if (!passenger && !driver && !admin) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed to cancel this ride");
		}
		transition(ride, RideStatus.CANCELLED);
		Ride saved = rideRepository.save(ride);
		releaseDriver(saved, bearerToken);
		eventPublisher.publishCancelled(saved);
		return RideResponse.from(saved);
	}

	public RideResponse get(String rideId, String userId, String role) {
		Ride ride = requireRide(rideId);
		assertCanView(ride, userId, role);
		return RideResponse.from(ride);
	}

	public List<RideResponse> listMine(String userId, String role) {
		List<Ride> rides = "DRIVER".equals(role)
				? rideRepository.findByDriverIdOrderByCreatedAtDesc(userId)
				: rideRepository.findByPassengerIdOrderByCreatedAtDesc(userId);
		return rides.stream().map(RideResponse::from).toList();
	}

	public void transition(Ride ride, RideStatus target) {
		if (ride.getStatus() == null || !ride.getStatus().canTransitionTo(target)) {
			throw new ApiException(HttpStatus.CONFLICT,
					"Invalid status transition from " + ride.getStatus() + " to " + target);
		}
		applyStatus(ride, target, Instant.now());
	}

	private void applyStatus(Ride ride, RideStatus status, Instant at) {
		ride.setStatus(status);
		ride.setUpdatedAt(at);
		ride.getHistory().add(new StatusChange(status, at));
	}

	private void releaseDriver(Ride ride, String bearerToken) {
		if (ride.getDriverId() != null) {
			try {
				peerClients.updateAvailability(ride.getDriverId(), "AVAILABLE", bearerToken);
			}
			catch (ApiException ignored) {
				// ride state is already persisted
			}
		}
	}

	private Ride requireRide(String id) {
		return rideRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ride not found"));
	}

	private void assertDriver(Ride ride, String driverId) {
		if (!driverId.equals(ride.getDriverId())) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Only the assigned driver can perform this action");
		}
	}

	private void assertCanView(Ride ride, String userId, String role) {
		if ("ADMIN".equals(role)) {
			return;
		}
		if (userId.equals(ride.getPassengerId()) || userId.equals(ride.getDriverId())) {
			return;
		}
		throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed to view this ride");
	}
}
