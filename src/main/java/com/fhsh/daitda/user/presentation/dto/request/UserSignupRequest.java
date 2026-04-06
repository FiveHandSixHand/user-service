package com.fhsh.daitda.user.presentation.dto.request;

import com.fhsh.daitda.user.domain.enums.UserRole;
import java.util.UUID;

public record UserSignupRequest(
        String email,
        String password,
        String name,
        UserRole role,
        String slackUserId,
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
