package com.carPooling.backend.service;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.AuthResponse;
import com.carPooling.backend.dto.response.CreatePasswordResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);

    AuthResponse refreshToken(RefreshTokenRequest req);


    GenricDTO<CreatePasswordResponse> createPassword(CreatePasswordRequest CreatePasswordRequest);


    void logout(LogoutRequest req);
}