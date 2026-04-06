package com.fhsh.daitda.user.presentation.controller.external;

import com.fhsh.daitda.response.CommonResponse;
import com.fhsh.daitda.user.application.command.LoginCommand;
import com.fhsh.daitda.user.application.result.LoginResult;
import com.fhsh.daitda.user.application.service.AuthService;
import com.fhsh.daitda.user.presentation.dto.request.LoginRequest;
import com.fhsh.daitda.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthExternalController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        LoginResult result = authService.login(command);
        return CommonResponse.success(new LoginResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        authService.logout(userId, authHeader);
        return CommonResponse.success();
    }
}
