package com.carPooling.backend.service;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.AuthResponse;
import com.carPooling.backend.dto.response.CreatePasswordResponse;
import com.carPooling.backend.dto.response.LogInResponse;
import com.carPooling.backend.dto.response.RefreshTokenResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest req);

    GenricDTO<LogInResponse>  login(LoginRequest req);

    GenricDTO<RefreshTokenResponse>  refreshToken(RefreshTokenRequest req);


    GenricDTO<CreatePasswordResponse> createPassword(CreatePasswordRequest CreatePasswordRequest);


    GenricDTO<Void> logout(LogoutRequest req);
}