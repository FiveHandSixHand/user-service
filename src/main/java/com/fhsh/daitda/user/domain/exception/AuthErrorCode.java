package com.fhsh.daitda.user.domain.exception;

import com.fhsh.daitda.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // 400 Bad Request
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 다시 로그인해주세요."),

    // 403 Forbidden
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "해당 작업을 수행할 권한이 없습니다."),

    // 409 Conflict
    AUTH_USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "인증 서버에 이미 등록된 계정입니다."),

    // 500 Internal Server Error
    AUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "인증 서버 통신 중 오류가 발생했습니다."),
    AUTH_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "인증 서버를 일시적으로 사용할 수 없습니다.");

    private final HttpStatus status;
    private final String description;
}