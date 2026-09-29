package com.ridelink.account.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.account.dto.AccountResponse;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.security.AuthPrincipal;
import com.ridelink.account.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
		return accountService.register(request);
	}

	@GetMapping("/me")
	public AccountResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
		return accountService.getById(principal.getUserId());
	}

	@PutMapping("/me")
	public AccountResponse updateMe(@AuthenticationPrincipal AuthPrincipal principal,
			@Valid @RequestBody UpdateProfileRequest request) {
		return accountService.updateProfile(principal.getUserId(), request);
	}

	@GetMapping("/{id}")
	public AccountResponse getById(@PathVariable String id) {
		return accountService.getById(id);
	}

	@PatchMapping("/{id}/status")
	public AccountResponse updateStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
		return accountService.updateStatus(id, request);
	}
}
