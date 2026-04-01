package com.fhsh.daitda.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("가입 거절");

    private final String description;
}
