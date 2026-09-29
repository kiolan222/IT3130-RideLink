package com.ridelink.ride.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FareEstimate(double amount, double distanceKm, double estimatedMinutes, String basis, String rule) {
}
