package com.fhsh.daitda.user.presentation.dto.request;

import java.util.UUID;

public record UserUpdateRequest(
        String name,
        String slackUserId,
        UUID hubId,
        UUID companyId
) {
}
