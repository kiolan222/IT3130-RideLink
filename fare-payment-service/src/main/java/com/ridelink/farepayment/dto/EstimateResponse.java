package com.ridelink.farepayment.dto;

public record EstimateResponse(double distanceKm, double estimatedMinutes, double amount, String basis, String rule) {
}
