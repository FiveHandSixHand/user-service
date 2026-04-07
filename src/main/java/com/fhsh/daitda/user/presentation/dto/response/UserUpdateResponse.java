package com.fhsh.daitda.user.presentation.dto.response;

import com.fhsh.daitda.user.application.result.UserQueryResult;
import com.fhsh.daitda.user.application.result.UserUpdateResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserUpdateResponse(
        UUID userId,
        String name,
        String slackUserId,
        UUID hubId,
        UUID companyId,
        LocalDateTime updatedAt
) {
    public static UserUpdateResponse from(UserUpdateResult result) {
        return new UserUpdateResponse(
                result.userId(),
                result.name(),
                result.slackUserId(),
                result.hubId(),
                result.companyId(),
                result.updatedAt()
        );
    }
}
