package com.ridelink.ride.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EligibleDriver(String id, String accountId, String serviceArea) {
}
