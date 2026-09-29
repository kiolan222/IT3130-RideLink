package com.ridelink.drivervehicle.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.drivervehicle.dto.AvailabilityRequest;
import com.ridelink.drivervehicle.dto.DriverResponse;
import com.ridelink.drivervehicle.dto.LocationRequest;
import com.ridelink.drivervehicle.dto.UpsertDriverRequest;
import com.ridelink.drivervehicle.security.AuthPrincipal;
import com.ridelink.drivervehicle.service.DriverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

	private final DriverService driverService;

	public DriverController(DriverService driverService) {
		this.driverService = driverService;
	}

	@PutMapping("/me")
	@PreAuthorize("hasRole('DRIVER')")
	public DriverResponse upsertMine(@AuthenticationPrincipal AuthPrincipal principal,
			@Valid @RequestBody UpsertDriverRequest request) {
		return driverService.upsertMine(principal.getUserId(), request);
	}

	@GetMapping("/me")
	@PreAuthorize("hasRole('DRIVER')")
	public DriverResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
		return driverService.getMine(principal.getUserId());
	}

	@PatchMapping("/me/availability")
	@PreAuthorize("hasRole('DRIVER')")
	public DriverResponse updateMyAvailability(@AuthenticationPrincipal AuthPrincipal principal,
			@Valid @RequestBody AvailabilityRequest request) {
		return driverService.updateAvailability(principal.getUserId(), request);
	}

	@PatchMapping("/me/location")
	@PreAuthorize("hasRole('DRIVER')")
	public DriverResponse updateMyLocation(@AuthenticationPrincipal AuthPrincipal principal,
			@Valid @RequestBody LocationRequest request) {
		return driverService.updateLocation(principal.getUserId(), request);
	}

	@GetMapping("/eligible")
	public List<DriverResponse> eligible(@RequestParam String area) {
		return driverService.findEligible(area);
	}

	@PatchMapping("/{accountId}/availability")
	public DriverResponse updateAvailability(@PathVariable String accountId,
			@Valid @RequestBody AvailabilityRequest request) {
		return driverService.updateAvailability(accountId, request);
	}
}
