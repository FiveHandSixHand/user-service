package com.fhsh.daitda.user.application.port;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface TokenPort {
    void saveRefreshToken(UUID userId, String refreshToken, long duration, TimeUnit unit);
    void deleteRefreshToken(UUID userId);
    String getRefreshToken(UUID userId);
    void addToBlacklist(String accessToken);
    boolean isBlacklisted(String accessToken);
    UUID getUserIdFromToken(String token);
}
