package com.fhsh.daitda.user.application.service;

import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.application.port.AccountPort;
import com.fhsh.daitda.user.application.port.TokenPort;
import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import com.fhsh.daitda.user.domain.exception.AuthErrorCode;
import com.fhsh.daitda.user.domain.exception.UserErrorCode;
import com.fhsh.daitda.user.domain.repository.UserRepository;
import com.fhsh.daitda.user.domain.vo.AuthTokens;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountPort accountPort;
    private final UserRepository userRepository;
    private final TokenPort tokenPort;

    public AuthTokens login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.APPROVED) {
            throw new BusinessException(UserErrorCode.USER_NOT_APPROVED);
        }

        AuthTokens authTokens = accountPort.authenticate(email, password);

        // Redis에 Refresh Token 저장
        tokenPort.saveRefreshToken(user.getUserId(), authTokens.refreshToken(), 7, TimeUnit.DAYS);

        return authTokens;
    }

    public void logout(UUID userId, String authHeader) {
        // Access Token에서 실제 사용자 ID 추출 및 검증
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

        String accessToken = authHeader.substring(7);
        UUID tokenId = tokenPort.getUserIdFromToken(accessToken);

        // 토큰의 sub(사용자ID)와 요청된 userId가 일치하는지 확인 (ID 변조 방지)
        if (tokenId == null || !tokenId.equals(userId)) {
            throw new BusinessException(AuthErrorCode.INSUFFICIENT_PERMISSION);
        }

        // Refresh Token 삭제 (토큰에 기록된 실제 소유자의 ID 사용)
        tokenPort.deleteRefreshToken(tokenId);

        // Access Token 블랙리스트 추가
        tokenPort.addToBlacklist(accessToken);
    }

    public AuthTokens reissue(String refreshToken) {
        // 토큰에서 사용자 ID 추출
        UUID userId = tokenPort.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
        }

        // Redis에 저장된 토큰과 일치하는지 확인
        String storedToken = tokenPort.getRefreshToken(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
        }

        // Keycloak을 통해 새로운 토큰 세트 발급
        AuthTokens tokens = accountPort.refresh(refreshToken);

        // Redis 정보 갱신
        tokenPort.saveRefreshToken(userId, tokens.refreshToken(), 7, TimeUnit.DAYS);

        return tokens;
    }
}
