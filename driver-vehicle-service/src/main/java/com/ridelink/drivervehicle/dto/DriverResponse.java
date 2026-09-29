package com.ridelink.drivervehicle.dto;

import com.ridelink.drivervehicle.domain.Availability;
import com.ridelink.drivervehicle.domain.DriverProfile;
import com.ridelink.drivervehicle.domain.GeoPoint;
import com.ridelink.drivervehicle.domain.Vehicle;

public record DriverResponse(String id, String accountId, Vehicle vehicle, String serviceArea, Availability availability,
		GeoPoint location) {

	public static DriverResponse from(DriverProfile profile) {
		return new DriverResponse(profile.getId(), profile.getAccountId(), profile.getVehicle(), profile.getServiceArea(),
				profile.getAvailability(), profile.getLocation());
	}
}
