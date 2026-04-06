package com.fhsh.daitda.user.application.service;

import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.application.command.LoginCommand;
import com.fhsh.daitda.user.application.port.AccountProvider;
import com.fhsh.daitda.user.application.port.TokenPort;
import com.fhsh.daitda.user.application.result.LoginResult;
import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import com.fhsh.daitda.user.domain.exception.UserErrorCode;
import com.fhsh.daitda.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountProvider accountProvider;
    private final UserRepository userRepository;
    private final TokenPort tokenPort;

    public LoginResult login(LoginCommand loginCommand) {
        User user = userRepository.findByEmail(loginCommand.email())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.APPROVED) {
            throw new BusinessException(UserErrorCode.USER_NOT_APPROVED);
        }

        LoginResult loginResult = accountProvider.authenticate(loginCommand.email(), loginCommand.password());

        // Redis에 Refresh Token 저장
        // 만료 시간은 예시로 7일 설정 (추후 설정 파일로 분리 가능)
        tokenPort.saveRefreshToken(user.getUserId(), loginResult.refreshToken(), 7, TimeUnit.DAYS);

        return loginResult;
    }
}
