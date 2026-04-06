package com.fhsh.daitda.user.application.port;

import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.vo.AuthTokens;

import java.util.UUID;

/**
 * keycloak과의 연동을 위한 인터페이스
 */
public interface AccountPort {
    UUID createAccount(String email, String password, String name, UserRole role);

    void deleteAccount(UUID accountId);

    AuthTokens authenticate(String email, String password);

    AuthTokens refresh(String refreshToken);

    void updateAccountStatus(UUID accountId, boolean enabled);

    void updateAccountRole(UUID accountId, UserRole newRole);
}
