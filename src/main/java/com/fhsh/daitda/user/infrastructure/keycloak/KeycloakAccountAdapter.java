package com.fhsh.daitda.user.infrastructure.keycloak;

import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.domain.exception.AuthErrorCode;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.application.port.AccountProvider;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KeycloakAccountAdapter implements AccountProvider {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public UUID createAccount(String email, String password, String name, UserRole role) {
        
        // 사용자 정보 설정
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(name);
        user.setEnabled(true);
        user.setEmailVerified(true);

        // 비밀번호 설정
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        // Keycloak에 사용자 생성 요청
        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(user);

        if (response.getStatus() != 201) {
            String errorMsg = response.readEntity(String.class);
            
            if (response.getStatus() == 409) {
                throw new BusinessException(AuthErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
            
            throw new BusinessException(AuthErrorCode.AUTH_SERVER_ERROR);
        }

        // 생성된 사용자의 UUID 추출
        String userId = CreatedResponseUtil.getCreatedId(response);

        // 권한(Role) 부여
        assignRoleToUser(userId, role.name());

        return UUID.fromString(userId);
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
}
