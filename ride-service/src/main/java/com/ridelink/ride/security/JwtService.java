package com.ridelink.ride.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.ridelink.ride.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final JwtProperties properties;

	public JwtService(JwtProperties properties) {
		this.properties = properties;
	}

	public String issueToken(String userId, String email, String role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + properties.getExpirationMs());
		return Jwts.builder()
				.subject(userId)
				.claim("email", email)
				.claim("role", role)
				.issuedAt(now)
				.expiration(expiry)
				.signWith(signingKey())
				.compact();
	}

	public AuthPrincipal parse(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return new AuthPrincipal(claims.getSubject(), claims.get("email", String.class),
				claims.get("role", String.class));
	}

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
	}
}
