package com.ridelink.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ridelink.account.config.JwtProperties;
import com.ridelink.account.domain.AccountStatus;
import com.ridelink.account.domain.Role;
import com.ridelink.account.domain.User;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.repo.UserRepository;
import com.ridelink.account.security.JwtService;
import com.ridelink.account.web.ApiException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
	private UserRepository userRepository;

	private AccountService accountService;

	@BeforeEach
	void setUp() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		JwtProperties properties = new JwtProperties();
		properties.setSecret("ridelink-dev-jwt-secret-change-me-32bytes");
		accountService = new AccountService(userRepository, encoder, new JwtService(properties));
	}

	@Test
	void registerRejectsAdminSelfSignup() {
		RegisterRequest request = new RegisterRequest("a@b.com", "password1", "A", "1", Role.ADMIN);
		assertThrows(ApiException.class, () -> accountService.register(request));
	}

	@Test
	void loginRejectsSuspendedAccount() {
		User user = new User();
		user.setId("u1");
		user.setEmail("a@b.com");
		user.setPasswordHash(new BCryptPasswordEncoder().encode("password1"));
		user.setRole(Role.PASSENGER);
		user.setStatus(AccountStatus.SUSPENDED);
		when(userRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(user));

		ApiException ex = assertThrows(ApiException.class,
				() -> accountService.login(new LoginRequest("a@b.com", "password1")));
		assertEquals(403, ex.getStatus().value());
	}

	@Test
	void registerPersistsPassenger() {
		when(userRepository.existsByEmailIgnoreCase("p@b.com")).thenReturn(false);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User user = invocation.getArgument(0);
			user.setId("id-1");
			return user;
		});
		var response = accountService.register(new RegisterRequest("p@b.com", "password1", "Pat", "077", Role.PASSENGER));
		assertEquals(Role.PASSENGER, response.role());
		assertEquals("id-1", response.id());
	}
}
