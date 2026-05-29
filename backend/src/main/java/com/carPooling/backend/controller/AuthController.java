package com.carPooling.backend.controller;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.*;
import com.carPooling.backend.service.AuthService;
import com.carPooling.backend.utils.StringConstant;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/password")
    public ResponseEntity<GenricDTO<CreatePasswordResponse>> createPassword(
            @Valid @RequestBody CreatePasswordRequest request
    ) {

        CreatePasswordResponse response = authService.createPassword(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new GenricDTO<>(
                        true,
                        "Password created successfully",
                        response
                )
        );
    }


    @PostMapping("/login")
    public ResponseEntity<GenricDTO<LogInResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LogInResponse response = authService.login(request);

        return ResponseEntity.ok(
                new GenricDTO<>(true ,"Login successful", response)
        );
    }


    @PostMapping("/refresh_token")
    public ResponseEntity<GenricDTO<RefreshTokenResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        RefreshTokenResponse response = authService.refreshToken(request);
        log.debug(
                "Token refreshed for user: {}, new access token: {}",
                response.getAccessToken()
        );
        return ResponseEntity.ok(
                new GenricDTO<>(
                        true,
                        "Token refreshed successfully",
                        response
                )
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<GenricDTO<Void>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(request);

        return ResponseEntity.ok(
                new GenricDTO<>(
                        true,
                        "Logout successful",
                        null
                )
        );
    }
}