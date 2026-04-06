package com.fhsh.daitda.user.presentation.dto.request;

import com.fhsh.daitda.user.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserSignupRequest(
        @Email @NotNull String email,
        @NotNull String password,
        @NotNull String name,
        @NotNull UserRole role,
        @NotNull String slackUserId,
        UUID hubId,
        UUID companyId
) {
    @Override
    public String toString() {
        return "UserSignupRequest[" +
                "email='" + email + '\'' +
                ", password='****'" + // 비밀번호 마스킹 처리
                ", name='" + name + '\'' +
                ", role=" + role +
                ", slackUserId='" + slackUserId + '\'' +
                ", hubId=" + hubId +
                ", companyId=" + companyId +
                ']';
    }
}
