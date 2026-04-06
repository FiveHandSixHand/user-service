package com.fhsh.daitda.user.presentation.dto.request;

public record LoginRequest(
        String email,
        String password
) {
    @Override
    public String toString() {
        return "LoginRequest[" +
                "email='" + email + '\'' +
                ", password='****'" + // 비밀번호 마스킹 처리
                ']';
    }
}
