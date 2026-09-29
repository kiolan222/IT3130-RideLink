package com.ridelink.farepayment.messaging;

public record RideLifecycleEvent(String rideId, String passengerId, String driverId, String pickup, String destination,
		Double pickupLat, Double pickupLng, Double destLat, Double destLng, String type) {
}
