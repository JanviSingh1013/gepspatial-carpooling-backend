package com.carPooling.backend.service;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.AuthResponse;
import com.carPooling.backend.dto.response.CreatePasswordResponse;
import com.carPooling.backend.dto.response.LogInResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest req);

    GenricDTO<LogInResponse>  login(LoginRequest req);

    AuthResponse refreshToken(RefreshTokenRequest req);


    GenricDTO<CreatePasswordResponse> createPassword(CreatePasswordRequest CreatePasswordRequest);


    void logout(LogoutRequest req);
}