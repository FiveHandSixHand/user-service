package com.fhsh.daitda.user.presentation.dto.request;

import com.fhsh.daitda.user.domain.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequest(
        @NotNull UserRole role
) {
}
