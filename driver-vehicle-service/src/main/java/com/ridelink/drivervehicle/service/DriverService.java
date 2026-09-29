package com.ridelink.drivervehicle.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ridelink.drivervehicle.domain.Availability;
import com.ridelink.drivervehicle.domain.DriverProfile;
import com.ridelink.drivervehicle.dto.AvailabilityRequest;
import com.ridelink.drivervehicle.dto.DriverResponse;
import com.ridelink.drivervehicle.dto.LocationRequest;
import com.ridelink.drivervehicle.dto.UpsertDriverRequest;
import com.ridelink.drivervehicle.repo.DriverProfileRepository;
import com.ridelink.drivervehicle.web.ApiException;

@Service
public class DriverService {

	private final DriverProfileRepository repository;

	public DriverService(DriverProfileRepository repository) {
		this.repository = repository;
	}

	public DriverResponse upsertMine(String accountId, UpsertDriverRequest request) {
		DriverProfile profile = repository.findByAccountId(accountId).orElseGet(DriverProfile::new);
		profile.setAccountId(accountId);
		profile.setVehicle(request.vehicle());
		profile.setServiceArea(request.serviceArea());
		if (request.location() != null) {
			profile.setLocation(request.location());
		}
		if (profile.getAvailability() == null) {
			profile.setAvailability(Availability.OFFLINE);
		}
		return DriverResponse.from(repository.save(profile));
	}

	public DriverResponse getMine(String accountId) {
		return DriverResponse.from(requireByAccount(accountId));
	}

	public DriverResponse updateAvailability(String accountId, AvailabilityRequest request) {
		DriverProfile profile = requireByAccount(accountId);
		profile.setAvailability(request.availability());
		return DriverResponse.from(repository.save(profile));
	}

	public DriverResponse updateLocation(String accountId, LocationRequest request) {
		DriverProfile profile = requireByAccount(accountId);
		profile.setLocation(request.location());
		return DriverResponse.from(repository.save(profile));
	}

	public List<DriverResponse> findEligible(String area) {
		if (area == null || area.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "area query parameter is required");
		}
		return repository.findByAvailabilityAndServiceAreaIgnoreCase(Availability.AVAILABLE, area.trim()).stream()
				.map(DriverResponse::from)
				.toList();
	}

	private DriverProfile requireByAccount(String accountId) {
		return repository.findByAccountId(accountId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Driver profile not found"));
	}
}
