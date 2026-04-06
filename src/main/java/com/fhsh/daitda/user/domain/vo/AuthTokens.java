package com.fhsh.daitda.user.domain.vo;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {
}