package com.ridelink.ride.dto;

import java.time.Instant;
import java.util.List;

import com.ridelink.ride.domain.Ride;
import com.ridelink.ride.domain.RideStatus;
import com.ridelink.ride.domain.StatusChange;

public record RideResponse(String id, String passengerId, String driverId, String pickup, String destination,
		Double pickupLat, Double pickupLng, Double destLat, Double destLng, RideStatus status, Double estimatedFare,
		Instant createdAt, Instant updatedAt, List<StatusChange> history) {

	public static RideResponse from(Ride ride) {
		return new RideResponse(ride.getId(), ride.getPassengerId(), ride.getDriverId(), ride.getPickup(),
				ride.getDestination(), ride.getPickupLat(), ride.getPickupLng(), ride.getDestLat(), ride.getDestLng(),
				ride.getStatus(), ride.getEstimatedFare(), ride.getCreatedAt(), ride.getUpdatedAt(), ride.getHistory());
	}
}
