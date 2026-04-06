package com.fhsh.daitda.user.infrastructure.external.redis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fhsh.daitda.user.application.port.TokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenAdapter implements TokenPort {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
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
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void addToBlacklist(String accessToken) {
        long remainingTtl = getRemainingTtlFromToken(accessToken);
        if (remainingTtl > 0) {
            String key = BLACKLIST_PREFIX + sha256(accessToken);
            redisTemplate.opsForValue().set(key, "logout", remainingTtl, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + sha256(accessToken);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public UUID getUserIdFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) return null;
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            JsonNode node = objectMapper.readTree(payload);
            String sub = node.get("sub").asText();
            return UUID.fromString(sub);
        } catch (Exception e) {
            return null;
        }
    }

    private long getRemainingTtlFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) return 0;
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            JsonNode node = objectMapper.readTree(payload);
            long exp = node.get("exp").asLong();
            long now = System.currentTimeMillis() / 1000;
            return Math.max(0, exp - now);
        } catch (Exception e) {
            return 0;
        }
    }

    private String sha256(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
