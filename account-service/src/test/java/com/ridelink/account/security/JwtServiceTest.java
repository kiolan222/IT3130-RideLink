package com.ridelink.account.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ridelink.account.config.JwtProperties;

class JwtServiceTest {

	@Test
	void roundTripIncludesRoleClaim() {
		JwtProperties properties = new JwtProperties();
		properties.setSecret("ridelink-dev-jwt-secret-change-me-32bytes");
		JwtService jwtService = new JwtService(properties);
		String token = jwtService.issueToken("abc", "a@b.com", "DRIVER");
		AuthPrincipal principal = jwtService.parse(token);
		assertEquals("abc", principal.getUserId());
		assertEquals("DRIVER", principal.getRole());
		assertEquals("a@b.com", principal.getUsername());
	}
}
