package com.ridelink.ride.web;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.security.AuthPrincipal;
import com.ridelink.ride.service.RideService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {

	private final RideService rideService;

	public RideController(RideService rideService) {
		this.rideService = rideService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('PASSENGER')")
	public RideResponse create(@AuthenticationPrincipal AuthPrincipal principal,
			@Valid @RequestBody CreateRideRequest request, HttpServletRequest httpRequest) {
		return rideService.create(principal.getUserId(), bearer(httpRequest), request);
	}

	@PostMapping("/{id}/accept")
	@PreAuthorize("hasRole('DRIVER')")
	public RideResponse accept(@PathVariable String id, @AuthenticationPrincipal AuthPrincipal principal) {
		return rideService.accept(id, principal.getUserId());
	}

	@PostMapping("/{id}/start")
	@PreAuthorize("hasRole('DRIVER')")
	public RideResponse start(@PathVariable String id, @AuthenticationPrincipal AuthPrincipal principal) {
		return rideService.start(id, principal.getUserId());
	}

	@PostMapping("/{id}/complete")
	@PreAuthorize("hasRole('DRIVER')")
	public RideResponse complete(@PathVariable String id, @AuthenticationPrincipal AuthPrincipal principal,
			HttpServletRequest httpRequest) {
		return rideService.complete(id, principal.getUserId(), bearer(httpRequest));
	}

	@PostMapping("/{id}/cancel")
	public RideResponse cancel(@PathVariable String id, @AuthenticationPrincipal AuthPrincipal principal,
			HttpServletRequest httpRequest) {
		return rideService.cancel(id, principal.getUserId(), principal.getRole(), bearer(httpRequest));
	}

	@GetMapping("/me")
	public List<RideResponse> mine(@AuthenticationPrincipal AuthPrincipal principal) {
		return rideService.listMine(principal.getUserId(), principal.getRole());
	}

	@GetMapping("/{id}")
	public RideResponse get(@PathVariable String id, @AuthenticationPrincipal AuthPrincipal principal) {
		return rideService.get(id, principal.getUserId(), principal.getRole());
	}

	private String bearer(HttpServletRequest request) {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		return header == null ? "" : header;
	}
}
