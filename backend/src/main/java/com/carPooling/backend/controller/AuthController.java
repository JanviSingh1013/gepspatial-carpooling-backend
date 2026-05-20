package com.carPooling.backend.controller;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.AuthResponse;
import com.carPooling.backend.dto.response.CreatePasswordResponse;
import com.carPooling.backend.dto.response.LogInResponse;
import com.carPooling.backend.dto.response.RefreshTokenResponse;
import com.carPooling.backend.service.AuthService;
import com.carPooling.backend.utils.StringConstant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/password")
    public ResponseEntity<GenricDTO<CreatePasswordResponse>>
    createPassword(
            @Valid @RequestBody CreatePasswordRequest request
    ) {

        GenricDTO<CreatePasswordResponse> response =
                authService.createPassword(request);

        if (StringConstant.SUCCESS.equalsIgnoreCase(response.getStatus())) {

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } else if (StringConstant.INVALID_REQUEST
                .equalsIgnoreCase(response.getStatus())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } else if (StringConstant.CONFLICT
                .equalsIgnoreCase(response.getStatus())) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

        } else {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<GenricDTO<LogInResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        GenricDTO<LogInResponse> response = authService.login(request);

        if (StringConstant.SUCCESS.equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } else if (StringConstant.INVALID_REQUEST
                .equalsIgnoreCase(response.getStatus())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }else if (StringConstant.UNAUTHORIZED
                .equalsIgnoreCase(response.getStatus())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }


    @PostMapping("/refresh_token")
    public ResponseEntity<GenricDTO<RefreshTokenResponse>> refreshToken(
            @Valid  @RequestBody RefreshTokenRequest request
    ) {

        GenricDTO<RefreshTokenResponse> response =
                authService.refreshToken(request);

        if (StringConstant.SUCCESS
                .equalsIgnoreCase(response.getStatus())) {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } else if (StringConstant.UNAUTHORIZED
                .equalsIgnoreCase(response.getStatus())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);

        } else {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse res = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res); // 201
    }





    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody LogoutRequest request
    ) {

        authService.logout(request);

        return ResponseEntity.ok("Logout successful");
    }
}