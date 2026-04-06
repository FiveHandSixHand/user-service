package com.fhsh.daitda.user.application.command;

import com.fhsh.daitda.user.domain.enums.UserRole;
import java.util.UUID;

public record UserRoleUpdateCommand(
        UUID userId,
        UserRole role
) {
}
