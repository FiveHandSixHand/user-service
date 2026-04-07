package com.fhsh.daitda.user.presentation.controller.external;

import com.fhsh.daitda.common.annotation.HasRole;
import com.fhsh.daitda.response.CommonResponse;
import com.fhsh.daitda.user.application.command.SignupCommand;
import com.fhsh.daitda.user.application.command.UserRoleUpdateCommand;
import com.fhsh.daitda.user.application.command.UserSearchCommand;
import com.fhsh.daitda.user.application.command.UserUpdateCommand;
import com.fhsh.daitda.user.application.result.UserQueryResult;
import com.fhsh.daitda.user.application.result.UserUpdateResult;
import com.fhsh.daitda.user.application.service.command.UserCommandService;
import com.fhsh.daitda.user.application.service.query.UserQueryService;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import com.fhsh.daitda.user.presentation.dto.request.*;
import com.fhsh.daitda.user.presentation.dto.response.UserResponse;
import com.fhsh.daitda.user.presentation.dto.response.UserUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @HasRole("ADMIN")
    @GetMapping("/{userId}")
    public CommonResponse<UserResponse> getUserById(@PathVariable("userId") UUID userId) {
        UserQueryResult result = userQueryService.getUserById(userId);
        return CommonResponse.success("사용자 상세 조회 성공", UserResponse.from(result));
    }

    @HasRole("ADMIN")
    @GetMapping
    public CommonResponse<Page<UserResponse>> getUsers(
            @ModelAttribute UserSearchRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UserSearchCommand command = new UserSearchCommand(
                request.email(),
                request.name(),
                request.role(),
                request.status(),
                request.hubId(),
                request.companyId(),
                request.createdAtFrom(),
                request.createdAtTo()
        );

        Page<UserQueryResult> results = userQueryService.getUsers(command, pageable);
        return CommonResponse.success("사용자 목록 조회 성공", results.map(UserResponse::from));
    }
}
