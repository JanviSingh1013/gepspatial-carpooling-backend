package com.carPooling.backend.service;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.*;

public interface AuthService {
    LogInResponse  login(LoginRequest req);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    CreatePasswordResponse createPassword(CreatePasswordRequest request);
    void logout(LogoutRequest request);}