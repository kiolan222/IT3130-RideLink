package com.ridelink.account.dto;

import com.ridelink.account.domain.AccountStatus;
import com.ridelink.account.domain.Role;
import com.ridelink.account.domain.User;

public record AccountResponse(String id, String email, String fullName, String phone, Role role, AccountStatus status) {

	public static AccountResponse from(User user) {
		return new AccountResponse(user.getId(), user.getEmail(), user.getFullName(), user.getPhone(), user.getRole(),
				user.getStatus());
	}
}
