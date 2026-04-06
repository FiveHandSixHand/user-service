package com.fhsh.daitda.user.presentation.controller.external;

import com.fhsh.daitda.response.CommonResponse;
import com.fhsh.daitda.user.application.command.SignupCommand;
import com.fhsh.daitda.user.application.service.command.UserCommandService;
import com.fhsh.daitda.user.presentation.dto.request.UserSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserExternalController {

    private final UserCommandService userCommandService;

    @PostMapping("/signup")
    public CommonResponse<UUID> signup(@Valid @RequestBody UserSignupRequest request) {
        SignupCommand command = new SignupCommand(
                request.email(),
                request.password(),
                request.name(),
                request.role(),
                request.slackUserId(),
                request.hubId(),
                request.companyId()
        );

        return CommonResponse.success(userCommandService.signup(command));
    }
}
