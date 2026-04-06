package com.fhsh.daitda.user.application.command;

import java.util.UUID;

public record UserUpdateCommand(
        UUID userId,
        String name,
        String slackUserId,
        UUID hubId,
        UUID companyId
) {
}
