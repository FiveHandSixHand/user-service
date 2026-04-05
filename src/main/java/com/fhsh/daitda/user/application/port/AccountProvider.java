package com.fhsh.daitda.user.application.port;

import com.fhsh.daitda.user.domain.enums.UserRole;
import java.util.UUID;

/**
 * keycloak과의 연동을 위한 인터페이스
 */
public interface AccountProvider {
    /**
     * keycloak에 계정을 생성하고 고유 식별자(UUID)를 반환합니다.
     */
    UUID createAccount(String email, String password, String name, UserRole role);

    /**
     * keycloak의 계정을 삭제합니다.
     */
    void deleteAccount(UUID accountId);
}
