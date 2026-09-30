package com.ridelink.drivervehicle.dto;

import com.ridelink.drivervehicle.domain.GeoPoint;

import jakarta.validation.constraints.NotNull;

public record LocationRequest(@NotNull GeoPoint location) {
}
