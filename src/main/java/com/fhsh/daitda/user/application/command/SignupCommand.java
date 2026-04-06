package com.fhsh.daitda.user.application.command;

import com.fhsh.daitda.user.domain.enums.UserRole;
import java.util.UUID;

public record SignupCommand(
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
        return "UserSignupCommand[" +
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
