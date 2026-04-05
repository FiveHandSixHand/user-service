package com.fhsh.daitda.user.application.command;

import com.fhsh.daitda.user.domain.enums.UserRole;
import java.util.UUID;

public record UserSignupCommand(
        String email,
        String password,
        String name,
        UserRole role,
        String slackUserId,
        UUID hubId,
        UUID companyId
) {
}
