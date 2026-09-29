package com.ridelink.ride.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RideStatusTest {

	@Test
	void allowsDocumentedHappyPath() {
		assertTrue(RideStatus.REQUESTED.canTransitionTo(RideStatus.ASSIGNED));
		assertTrue(RideStatus.ASSIGNED.canTransitionTo(RideStatus.ACCEPTED));
		assertTrue(RideStatus.ACCEPTED.canTransitionTo(RideStatus.IN_PROGRESS));
		assertTrue(RideStatus.IN_PROGRESS.canTransitionTo(RideStatus.COMPLETED));
	}

	@Test
	void rejectsInvalidJumps() {
		assertFalse(RideStatus.REQUESTED.canTransitionTo(RideStatus.COMPLETED));
		assertFalse(RideStatus.IN_PROGRESS.canTransitionTo(RideStatus.CANCELLED));
		assertFalse(RideStatus.COMPLETED.canTransitionTo(RideStatus.CANCELLED));
	}
}
