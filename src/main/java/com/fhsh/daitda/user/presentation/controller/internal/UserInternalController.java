package com.fhsh.daitda.user.presentation.controller.internal;

import com.fhsh.daitda.response.CommonResponse;
import com.fhsh.daitda.user.application.result.UserQueryResult;
import com.fhsh.daitda.user.application.service.query.UserQueryService;
import com.fhsh.daitda.user.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/users")
public class UserInternalController {
    private final UserQueryService userQueryService;

    @GetMapping("/{userId}")
    public CommonResponse<UserResponse> getUserById(@PathVariable("userId") UUID userId) {
        UserQueryResult result = userQueryService.getUserById(userId);
        return CommonResponse.success(UserResponse.from(result));
    }
}
