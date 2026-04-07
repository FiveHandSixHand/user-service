package com.fhsh.daitda.user.application.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserUpdateResult(
    UUID userId,
    String name,
    String slackUserId,
    UUID hubId,
    UUID companyId,
    LocalDateTime updatedAt
) {

}
