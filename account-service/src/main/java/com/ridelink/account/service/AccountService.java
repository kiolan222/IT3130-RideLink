package com.ridelink.account.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ridelink.account.domain.AccountStatus;
import com.ridelink.account.domain.Role;
import com.ridelink.account.domain.User;
import com.ridelink.account.dto.AccountResponse;
import com.ridelink.account.dto.AuthResponse;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.repo.UserRepository;
import com.ridelink.account.security.JwtService;
import com.ridelink.account.web.ApiException;

@Service
public class AccountService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AccountService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public AccountResponse register(RegisterRequest request) {
		if (request.role() == Role.ADMIN) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot self-register as ADMIN");
		}
		if (userRepository.existsByEmailIgnoreCase(request.email())) {
			throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
		}
		User user = new User();
		user.setEmail(request.email().toLowerCase());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setFullName(request.fullName());
		user.setPhone(request.phone());
		user.setRole(request.role());
		user.setStatus(AccountStatus.ACTIVE);
		return AccountResponse.from(userRepository.save(user));
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmailIgnoreCase(request.email())
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
		if (user.getStatus() == AccountStatus.SUSPENDED) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Account is suspended");
		}
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		String token = jwtService.issueToken(user.getId(), user.getEmail(), user.getRole().name());
		return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name());
	}

	public AccountResponse getById(String id) {
		return AccountResponse.from(requireUser(id));
	}

	public AccountResponse updateProfile(String userId, UpdateProfileRequest request) {
		User user = requireUser(userId);
		user.setFullName(request.fullName());
		user.setPhone(request.phone());
		return AccountResponse.from(userRepository.save(user));
	}

	public AccountResponse updateStatus(String id, UpdateStatusRequest request) {
		User user = requireUser(id);
		user.setStatus(request.status());
		return AccountResponse.from(userRepository.save(user));
	}

	private User requireUser(String id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));
	}
}
