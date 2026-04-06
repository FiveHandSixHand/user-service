package com.fhsh.daitda.user.presentation.dto.response;

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
}
