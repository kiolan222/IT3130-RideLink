package com.ridelink.ride.domain;

public enum RideStatus {
	REQUESTED,
	ASSIGNED,
	ACCEPTED,
	IN_PROGRESS,
	COMPLETED,
	CANCELLED;

	public boolean canTransitionTo(RideStatus target) {
		return switch (this) {
			case REQUESTED -> target == ASSIGNED || target == CANCELLED;
			case ASSIGNED -> target == ACCEPTED || target == CANCELLED;
			case ACCEPTED -> target == IN_PROGRESS || target == CANCELLED;
			case IN_PROGRESS -> target == COMPLETED;
			case COMPLETED, CANCELLED -> false;
		};
	}
}
