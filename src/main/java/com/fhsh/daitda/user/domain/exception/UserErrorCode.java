package com.fhsh.daitda.user.domain.exception;

import com.fhsh.daitda.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // 400 Bad Request
    INVALID_USER_INPUT(HttpStatus.BAD_REQUEST, "잘못된 사용자 입력 값입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "올바르지 않은 이메일 형식입니다."),
    AUDITOR_INFO_TOO_LONG(HttpStatus.BAD_REQUEST, "수정자 정보가 허용된 길이를 초과했습니다."),

    // 401 Unauthorized / 403 Forbidden
    USER_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 사용자입니다."),
    USER_DELETED(HttpStatus.FORBIDDEN, "삭제된 사용자 계정입니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),

    // 409 Conflict
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제 처리된 사용자입니다."),
    ALREADY_APPROVED(HttpStatus.CONFLICT, "이미 승인된 사용자입니다.");

    private final HttpStatus status;
    private final String description;
}