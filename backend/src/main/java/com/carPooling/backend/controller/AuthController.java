package com.carPooling.backend.controller;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.AuthResponse;
import com.carPooling.backend.dto.response.CreatePasswordResponse;
import com.carPooling.backend.dto.response.LogInResponse;
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

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse res = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res); // 201
    }



    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody LogoutRequest request
    ) {

        authService.logout(request);

        return ResponseEntity.ok("Logout successful");
    }
}

/**
 * // COMPREHENSIVE PASSWORD MANAGEMENT API
 * <p>
 * // 1. SET PASSWORD (First time, signup)
 * POST /api/users
 * {
 * "email": "john@example.com",
 * "password": "SecurePass!123"
 * }
 * Response: 201 Created
 * {
 * "id": 123,
 * "email": "john@example.com",
 * "message": "Account created with password"
 * }
 * <p>
 * // 2. CHANGE PASSWORD (User logged in, knows old password)
 * PUT /api/users/123/password
 * {
 * "old_password": "SecurePass!123",
 * "new_password": "NewPass!456"
 * }
 * Response: 200 OK
 * {
 * "message": "Password changed successfully",
 * "changed_at": "2024-01-15T10:30:00Z"
 * }
 * <p>
 * // 3. REQUEST PASSWORD RESET (Forgot password)
 * POST /api/password-reset
 * {
 * "email": "john@example.com"
 * }
 * Response: 202 Accepted
 * {
 * "message": "Check your email for reset instructions",
 * "reset_email_sent": true
 * }
 * <p>
 * // 4. VERIFY RESET TOKEN (Optional: GET to check if valid)
 * GET /api/password-reset/abc123def456
 * Response: 200 OK
 * {
 * "valid": true,
 * "expires_at": "2024-01-15T12:30:00Z"
 * }
 * <p>
 * // 5. CONFIRM PASSWORD RESET (New password without old one)
 * POST /api/password-reset/abc123def456
 * {
 * "new_password": "FreshPass!789"
 * }
 * Response: 200 OK
 * {
 * "message": "Password reset successfully",
 * "user_id": 123
 * }
 * <p>
 * // 6. INVALIDATE PASSWORD (Logout, security issue, etc)
 * DELETE /api/users/123/password
 * {
 * "reason": "user_logout"  // optional
 * }
 * Response: 204 No Content
 * <p>
 * // 7. CHECK PASSWORD STRENGTH (Before submitting)
 * POST /api/password-strength
 * {
 * "password": "MyPassword123"
 * }
 * Response: 200 OK
 * {
 * "strength": "strong",
 * "score": 85,
 * "issues": []
 * }
 * <p>
 * // 8. GET PASSWORD REQUIREMENTS
 * GET /api/password-requirements
 * Response: 200 OK
 * {
 * "min_length": 8,
 * "require_uppercase": true,
 * "require_lowercase": true,
 * "require_numbers": true,
 * "require_special_chars": true,
 * "special_chars": "!@#$%^&*"
 * }
 */