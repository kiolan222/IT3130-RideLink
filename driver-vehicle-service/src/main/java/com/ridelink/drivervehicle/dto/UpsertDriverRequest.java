package com.ridelink.drivervehicle.dto;

import com.ridelink.drivervehicle.domain.GeoPoint;
import com.ridelink.drivervehicle.domain.Vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpsertDriverRequest(@NotNull Vehicle vehicle, @NotBlank String serviceArea, GeoPoint location) {
}
