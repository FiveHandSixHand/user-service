package com.fhsh.daitda.user.presentation.controller.external;

import com.fhsh.daitda.common.annotation.HasRole;
import com.fhsh.daitda.response.CommonResponse;
import com.fhsh.daitda.user.application.command.SignupCommand;
import com.fhsh.daitda.user.application.command.UserRoleUpdateCommand;
import com.fhsh.daitda.user.application.command.UserUpdateCommand;
import com.fhsh.daitda.user.application.result.UserQueryResult;
import com.fhsh.daitda.user.application.result.UserUpdateResult;
import com.fhsh.daitda.user.application.service.command.UserCommandService;
import com.fhsh.daitda.user.application.service.query.UserQueryService;
import com.fhsh.daitda.user.presentation.dto.request.UserRegistrationRequest;
import com.fhsh.daitda.user.presentation.dto.request.UserRoleUpdateRequest;
import com.fhsh.daitda.user.presentation.dto.request.UserSignupRequest;
import com.fhsh.daitda.user.presentation.dto.request.UserUpdateRequest;
import com.fhsh.daitda.user.presentation.dto.response.UserResponse;
import com.fhsh.daitda.user.presentation.dto.response.UserUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserExternalController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/signup")
    public CommonResponse<Void> signup(@Valid @RequestBody UserSignupRequest request) {
        SignupCommand command = new SignupCommand(
                request.email(),
                request.password(),
                request.name(),
                request.role(),
                request.slackUserId(),
                request.hubId(),
                request.companyId()
        );
        userCommandService.signup(command);
        return CommonResponse.success();
    }

    @HasRole({"ADMIN", "HUB_ADMIN"})
    @PostMapping("/{userId}/registration")
    public CommonResponse<Void> registration(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserRegistrationRequest request
    ) {
        userCommandService.registration(userId, request.isApproved());
        return CommonResponse.success();
    }

    @HasRole("ADMIN")
    @DeleteMapping("/{userId}")
    public CommonResponse<Void> deleteUser(
            @PathVariable("userId") UUID userId,
            @RequestHeader("X-User-Id") UUID requesterId
    ) {
        userCommandService.deleteUser(userId, requesterId);
        return CommonResponse.success();
    }

    @HasRole("ADMIN")
    @PatchMapping("/{userId}")
    public CommonResponse<UserUpdateResponse> updateUser(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserUpdateResult result = userCommandService.updateUser(new UserUpdateCommand(
                userId,
                request.name(),
                request.slackUserId(),
                request.hubId(),
                request.companyId()
        ));
        return CommonResponse.success("사용자 정보 수정 성공", UserUpdateResponse.from(result));
    }

    @HasRole("ADMIN")
    @PatchMapping("/{userId}/role")
    public CommonResponse<Void> updateUserRole(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserRoleUpdateRequest request
    ) {
        userCommandService.updateUserRole(new UserRoleUpdateCommand(userId, request.role()));
        return CommonResponse.success("사용자 권한 변경 성공", null);
    }

    @GetMapping("/me")
    public CommonResponse<UserResponse> getMyInfo(@RequestHeader("X-User-Id") UUID userId) {
        UserQueryResult result = userQueryService.getUserById(userId);
        return CommonResponse.success("내 정보 조회 성공", UserResponse.from(result));
    }
}
