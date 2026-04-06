package com.fhsh.daitda.user.application.service;

import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.application.command.LoginCommand;
import com.fhsh.daitda.user.application.port.AccountPort;
import com.fhsh.daitda.user.application.port.TokenPort;
import com.fhsh.daitda.user.application.result.LoginResult;
import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import com.fhsh.daitda.user.domain.exception.AuthErrorCode;
import com.fhsh.daitda.user.domain.exception.UserErrorCode;
import com.fhsh.daitda.user.domain.repository.UserRepository;
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

    public LoginResult login(LoginCommand loginCommand) {
        User user = userRepository.findByEmail(loginCommand.email())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.APPROVED) {
            throw new BusinessException(UserErrorCode.USER_NOT_APPROVED);
        }

        LoginResult loginResult = accountPort.authenticate(loginCommand.email(), loginCommand.password());

        // Redis에 Refresh Token 저장
        tokenPort.saveRefreshToken(user.getUserId(), loginResult.refreshToken(), 7, TimeUnit.DAYS);

        return loginResult;
    }

    public void logout(UUID userId, String authHeader) {
        // 1. Refresh Token 삭제
        tokenPort.deleteRefreshToken(userId);

        // 2. Access Token 블랙리스트 추가 요청
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenPort.addToBlacklist(authHeader.substring(7));
        }
    }

    public LoginResult reissue(String refreshToken) {
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
        LoginResult newResult = accountPort.refresh(refreshToken);

        // Redis 정보 갱신
        tokenPort.saveRefreshToken(userId, newResult.refreshToken(), 7, TimeUnit.DAYS);

        return newResult;
    }
}
