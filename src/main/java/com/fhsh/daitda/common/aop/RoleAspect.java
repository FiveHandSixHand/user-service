package com.fhsh.daitda.common.aop;

import com.fhsh.daitda.common.annotation.HasRole;
import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.domain.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleAspect {

    private final HttpServletRequest request;

    @Before("@annotation(hasRole)")
    public void checkRole(HasRole hasRole) {
        // 허용된 권한 리스트 가져오기 (예: ["ADMIN", "HUB_ADMIN"])
        List<String> allowedRoles = Arrays.asList(hasRole.value());

        String userRole = request.getHeader("X-User-Role");

        if (userRole == null || !allowedRoles.contains(userRole)) {
            throw new BusinessException(AuthErrorCode.INSUFFICIENT_PERMISSION);
        }
    }
}