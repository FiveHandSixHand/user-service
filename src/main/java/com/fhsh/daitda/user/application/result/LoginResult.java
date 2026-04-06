package com.fhsh.daitda.user.application.result;

public record LoginResult(
        String accessToken,
        String refreshToken
) {
}
