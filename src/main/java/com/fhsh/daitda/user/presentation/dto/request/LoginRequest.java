package com.fhsh.daitda.user.presentation.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
