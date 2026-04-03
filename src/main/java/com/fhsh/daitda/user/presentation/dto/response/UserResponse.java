package com.fhsh.daitda.user.presentation.dto.response;

import com.fhsh.daitda.user.application.result.UserQueryResult;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String name,
        UserRole role,
        UserStatus status,
        String slackUserId,
        UUID hubId,
        UUID companyId
) {
    public static UserResponse from(UserQueryResult result) {
        return new UserResponse(
                result.userId(),
                result.email(),
                result.name(),
                result.role(),
                result.status(),
                result.slackUserId(),
                result.hubId(),
                result.companyId()
        );
    }
}
