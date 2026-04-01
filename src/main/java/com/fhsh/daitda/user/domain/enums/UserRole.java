package com.fhsh.daitda.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    MASTER("마스터 관리자"),
    HUB_ADMIN("허브 관리자"),
    DELIVERY("배송 담당자"),
    COMPANY("업체 담당자");

    private final String description;
}
