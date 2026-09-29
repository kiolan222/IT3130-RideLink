package com.ridelink.account.dto;

import com.ridelink.account.domain.AccountStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull AccountStatus status) {
}
