package com.fhsh.daitda.user.presentation.controller.external;

import com.fhsh.daitda.response.CommonResponse;
import com.fhsh.daitda.user.application.service.AuthService;
import com.fhsh.daitda.user.domain.vo.AuthTokens;
import com.fhsh.daitda.user.presentation.dto.request.LoginRequest;
import com.fhsh.daitda.user.presentation.dto.request.TokenReissueRequest;
import com.fhsh.daitda.user.presentation.dto.response.LoginResponse;
import jakarta.validation.Valid;
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
        AuthTokens result = authService.login(request.email(), request.password());
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

    @PostMapping("/reissue")
    public CommonResponse<LoginResponse> reissue(@Valid @RequestBody TokenReissueRequest request) {
        AuthTokens result = authService.reissue(request.refreshToken());
        return CommonResponse.success(new LoginResponse(result.accessToken(), result.refreshToken()));
    }
}
