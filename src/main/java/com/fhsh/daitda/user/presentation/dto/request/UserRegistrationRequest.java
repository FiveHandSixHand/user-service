package com.fhsh.daitda.user.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserRegistrationRequest(
        @NotNull Boolean isApproved
) {
}
