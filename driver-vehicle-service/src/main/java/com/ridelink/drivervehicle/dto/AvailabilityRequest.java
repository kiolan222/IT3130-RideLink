package com.ridelink.drivervehicle.dto;

import com.ridelink.drivervehicle.domain.Availability;

import jakarta.validation.constraints.NotNull;

public record AvailabilityRequest(@NotNull Availability availability) {
}
