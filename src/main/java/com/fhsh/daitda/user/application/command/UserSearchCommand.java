package com.fhsh.daitda.user.application.command;

import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserSearchCommand(
        String email,
        String name,
        UserRole role,
        UserStatus status,
        UUID hubId,
        UUID companyId,
        LocalDateTime createdAtFrom,
        LocalDateTime createdAtTo
) {
}
