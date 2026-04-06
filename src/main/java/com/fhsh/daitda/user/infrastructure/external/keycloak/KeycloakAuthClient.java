package com.fhsh.daitda.user.infrastructure.external.keycloak;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.keycloak.representations.AccessTokenResponse;
import java.util.Map;

/**
 * Keycloak OIDC 토큰 엔드포인트와 직접 통신하기 위한 Feign Client
 */
@FeignClient(name = "keycloak-auth-client", url = "${keycloak.server-url}")
public interface KeycloakAuthClient {

    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token", 
                 consumes = "application/x-www-form-urlencoded")
    AccessTokenResponse refresh(Map<String, ?> params);
}
