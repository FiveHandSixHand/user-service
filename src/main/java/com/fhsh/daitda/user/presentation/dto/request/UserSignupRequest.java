package com.fhsh.daitda.user.presentation.dto.request;

import com.fhsh.daitda.user.domain.enums.UserRole;
import java.util.UUID;

public record UserSignupRequest(
        String email,
        String password,
        String name,
        UserRole role,
        String slackUserId,
        UUID hubId,
        UUID companyId
) {
}
