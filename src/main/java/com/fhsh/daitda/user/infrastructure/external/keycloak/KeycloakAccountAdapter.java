package com.fhsh.daitda.user.infrastructure.external.keycloak;

import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.application.port.AccountPort;
import com.fhsh.daitda.user.domain.exception.AuthErrorCode;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.vo.AuthTokens;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeycloakAccountAdapter implements AccountPort {

    private final Keycloak keycloak;
    private final KeycloakAuthClient authClient;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.user.client-id}")
    private String userClientId;

    @Override
    public UUID createAccount(String email, String password, String name, UserRole role) {
        
        // 사용자 정보 설정
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(name);
        user.setLastName(name);
        user.setEnabled(false);
        user.setEmailVerified(true);

        // 비밀번호 설정
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        // Keycloak에 사용자 생성 요청
        UsersResource usersResource = keycloak.realm(realm).users();

        String createdId = null;

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() != 201) {
                if (response.getStatus() == 409) throw new BusinessException(AuthErrorCode.AUTH_USER_ALREADY_EXISTS);
                throw new BusinessException(AuthErrorCode.AUTH_SERVER_ERROR);
            }
            // 생성된 ID 저장
            createdId = CreatedResponseUtil.getCreatedId(response);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.AUTH_SERVER_UNAVAILABLE);
        }

        // 권한(Role) 부여 시도
        try {
            assignRoleToUser(createdId, role.name());
        } catch (Exception e) {
            if (createdId != null) {
                deleteAccount(UUID.fromString(createdId));
            }
            throw new BusinessException(AuthErrorCode.AUTH_SERVER_ERROR);
        }

        return UUID.fromString(createdId);
    }

    private void assignRoleToUser(String userId, String roleName) {
        try {
            // Realm에서 Role 정보 조회
            RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            
            // 사용자에게 Role 추가
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            userResource.roles().realmLevel().add(Collections.singletonList(role));

        } catch (Exception e) {
            // 권한 부여 실패 시 계정 생성 자체를 실패로 간주하고 삭제하는 보상 로직이 작동하도록 예외를 던짐
            throw new BusinessException(AuthErrorCode.AUTH_SERVER_ERROR);
        }
    }

    @Override
    public void deleteAccount(UUID accountId) {
        try {
            keycloak.realm(realm).users().get(accountId.toString()).remove();
        }
        catch (Exception e) {
            throw new BusinessException(AuthErrorCode.AUTH_SERVER_ERROR);
        }
    }

    @Override
    public AuthTokens authenticate(String email, String password) {
        try (Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(userClientId)
                .username(email)
                .password(password)
                .build()
        ) {
            AccessTokenResponse response = userKeycloak.tokenManager().getAccessToken();
            return new AuthTokens(response.getToken(), response.getRefreshToken());
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "refresh_token");
            params.put("client_id", userClientId);
            params.put("refresh_token", refreshToken);

            AccessTokenResponse response = authClient.refresh(params);
            return new AuthTokens(response.getToken(), response.getRefreshToken());
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
        }
    }

    @Override
    public void updateAccountStatus(UUID accountId, boolean enabled) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(accountId.toString());
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.AUTH_SERVER_ERROR);
        }
    }

    @Override
    public void updateAccountRole(UUID accountId, UserRole newRole) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(accountId.toString());

            // 현재 사용자에게 부여된 렐름 레벨 권한 목록 조회
            List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll();

            // 우리 애플리케이션에서 사용하는 권한(UserRole enum에 정의된 것들) 필터링
            Set<String> appRoleNames = Arrays.stream(UserRole.values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());

            // 변경하려는 대상 권한을 이미 가지고 있는지 확인
            boolean alreadyHasTargetRole = currentRoles.stream()
                    .anyMatch(role -> newRole.name().equals(role.getName()));
            if (!alreadyHasTargetRole) {
                assignRoleToUser(accountId.toString(), newRole.name());
            }

            List<RoleRepresentation> rolesToRemove = currentRoles.stream()
                    .filter(role -> appRoleNames.contains(role.getName()))
                    .filter(role -> !newRole.name().equals(role.getName()))
                    .toList();
            if (!rolesToRemove.isEmpty()) {
                userResource.roles().realmLevel().remove(rolesToRemove);
            }

        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.AUTH_SERVER_ERROR);
        }
    }
}
