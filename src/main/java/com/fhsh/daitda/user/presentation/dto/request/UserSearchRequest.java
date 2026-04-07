package com.fhsh.daitda.user.presentation.dto.request;

import com.fhsh.daitda.user.application.result.UserQueryResult;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserSearchRequest(
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
