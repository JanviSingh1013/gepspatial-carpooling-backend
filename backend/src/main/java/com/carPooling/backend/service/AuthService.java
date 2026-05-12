package com.carPooling.backend.service;

import com.carPooling.backend.dto.request.LoginRequest;
import com.carPooling.backend.dto.request.RegisterRequest;
import com.carPooling.backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);
}