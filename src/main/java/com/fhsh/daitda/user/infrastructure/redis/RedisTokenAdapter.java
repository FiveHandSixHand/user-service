package com.fhsh.daitda.user.infrastructure.redis;

import com.fhsh.daitda.user.application.port.TokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenAdapter implements TokenPort {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    public void saveRefreshToken(UUID userId, String refreshToken, long duration, TimeUnit unit) {
        String key = REFRESH_TOKEN_PREFIX + userId.toString();
        redisTemplate.opsForValue().set(key, refreshToken, duration, unit);
    }

    @Override
    public void deleteRefreshToken(UUID userId) {
        String key = REFRESH_TOKEN_PREFIX + userId.toString();
        redisTemplate.delete(key);
    }

    @Override
    public String getRefreshToken(UUID userId) {
        String key = REFRESH_TOKEN_PREFIX + userId.toString();
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void addToBlacklist(String accessToken, long duration, TimeUnit unit) {
        String key = BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "blacklisted", duration, unit);
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
