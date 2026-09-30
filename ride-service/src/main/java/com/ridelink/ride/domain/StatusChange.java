package com.ridelink.ride.domain;

import java.time.Instant;

public class StatusChange {

	private RideStatus status;
	private Instant at;

	public StatusChange() {
	}

	public StatusChange(RideStatus status, Instant at) {
		this.status = status;
		this.at = at;
	}

	public RideStatus getStatus() {
		return status;
	}

	public void setStatus(RideStatus status) {
		this.status = status;
	}

	public Instant getAt() {
		return at;
	}

	public void setAt(Instant at) {
		this.at = at;
	}
}
