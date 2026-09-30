package com.ridelink.ride.web;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String error, String message) {
}
