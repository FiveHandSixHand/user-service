package com.fhsh.daitda.user.application.result;

import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import java.util.UUID;

public record UserQueryResult(
        UUID userId,
        String email,
        String name,
        UserRole role,
        UserStatus status,
        String slackUserId,
        UUID hubId,
        UUID companyId
) {
    public static UserQueryResult from(User user) {
        return new UserQueryResult(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getStatus(),
                user.getSlackUserId(),
                user.getHubId(),
                user.getCompanyId()
        );
    }
}
