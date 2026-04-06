package com.fhsh.daitda.user.application.command;

public record LoginCommand(
        String email,
        String password
) {
    @Override
    public String toString() {
        return "LoginCommand[" +
                "email='" + email + '\'' +
                ", password='****'" + // 비밀번호 마스킹 처리
                ']';
    }
}
