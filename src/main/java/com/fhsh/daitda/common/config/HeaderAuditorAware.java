package com.fhsh.daitda.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Component
public class HeaderAuditorAware implements AuditorAware<UUID> {

    // Gateway에서 내려주는 사용자 ID 헤더 이름
    private static final String HEADER_USER_ID = "X-User-Id";

    @Override
    public Optional<UUID> getCurrentAuditor() {
        // 현재 요청 스레드에 바인딩된 RequestAttributes를 가져옴
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // HTTP 요청이 없는 상황이면 현재 사용자 정보를 알 수 없으므로 빈 값 반환
        if (attributes == null) {
            return Optional.empty();
        }

        // 실제 HttpServletRequest 객체 꺼냄
        HttpServletRequest request = attributes.getRequest();
        // 요청 헤더에서 X-User-Id 값 읽어옴
        String userId = request.getHeader(HEADER_USER_ID);

        // 헤더가 없거나 값이 비어 있으면 감사자 정보를 알 수 없으므로 빈 값 반환
        if (userId == null || userId.isBlank()) {
            return Optional.empty();
        }

        try {
            // 문자열 형태의 userId를 UUID로 변환해서 반환하고, @CreatedBy, @LastModifiedBy 필드에 자동 반영됨
            return Optional.of(UUID.fromString(userId));
        } catch (IllegalArgumentException e) {
            // 헤더 값이 UUID 형식이 아니면 예외를 터뜨리지 않고 빈 값 반환
            return Optional.empty();
        }
    }
}
