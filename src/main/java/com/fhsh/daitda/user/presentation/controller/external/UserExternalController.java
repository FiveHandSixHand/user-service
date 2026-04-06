package com.fhsh.daitda.user.presentation.controller.external;

import com.fhsh.daitda.common.annotation.HasRole;
import com.fhsh.daitda.response.CommonResponse;
import com.fhsh.daitda.user.application.command.SignupCommand;
import com.fhsh.daitda.user.application.service.command.UserCommandService;
import com.fhsh.daitda.user.presentation.dto.request.UserRegistrationRequest;
import com.fhsh.daitda.user.presentation.dto.request.UserSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserExternalController {

    private final UserCommandService userCommandService;

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

    @HasRole("ADMIN")
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
}
