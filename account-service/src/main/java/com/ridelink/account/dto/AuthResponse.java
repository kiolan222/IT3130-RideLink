package com.ridelink.account.dto;

public record AuthResponse(String token, String userId, String email, String role) {
}
