package com.carPooling.backend.service.impl;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.CreatePasswordResponse;
import com.carPooling.backend.dto.response.LogInResponse;
import com.carPooling.backend.dto.response.RefreshTokenResponse;
import com.carPooling.backend.dto.response.UpdateProfileResponse;
import com.carPooling.backend.entity.RefreshToken;
import com.carPooling.backend.entity.User;
import com.carPooling.backend.exception.custom_exception.InvalidRequestException;
import com.carPooling.backend.exception.custom_exception.UnauthorizedException;
import com.carPooling.backend.repository.RefreshTokenRepository;
import com.carPooling.backend.repository.UserRepository;
import com.carPooling.backend.security.JwtUtil;
import com.carPooling.backend.service.AuthService;
import com.carPooling.backend.utils.StringConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j                          // ← Lombok generates: private static final Logger log = ... Simple Logging Facade
@Service
@RequiredArgsConstructor
public class AuthServiceImplements implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public CreatePasswordResponse createPassword(CreatePasswordRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("Password and confirm password do not match");
        }

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        User user;

        if (optionalUser.isEmpty()) {
            user = new User();
            user.setEmail(request.getEmail());
        } else {
            user = optionalUser.get();
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());

        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        CreatePasswordResponse response = new CreatePasswordResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);

        return response;
    }

    @Override
    @Transactional
    public LogInResponse login(LoginRequest req) {

        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = optionalUser.get();

        boolean isPasswordCorrect = passwordEncoder.matches(req.getPassword(), user.getPassword());

        if (!isPasswordCorrect) {
            throw new UnauthorizedException("Password is incorrect");
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());

        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder().token(refreshTokenValue).expiryDate(LocalDateTime.now().plusDays(7)).revoked(false).user(user).build();

        refreshTokenRepository.save(refreshToken);

        LogInResponse response = new LogInResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);
        response.setUser(user);

        return response;
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        String refreshTokenString = request.getRefreshToken();

        log.debug(
                "Received refresh token request with token: {}",
                refreshTokenString
        );

        Optional<RefreshToken> optionalRefreshToken =
                refreshTokenRepository.findByToken(refreshTokenString);

        // Token not found
        if (optionalRefreshToken.isEmpty()) {
            throw new UnauthorizedException("Unauthorized Access - try to login/register first");
        }

        RefreshToken storedToken = optionalRefreshToken.get();

        // Token revoked
        if (storedToken.isRevoked()) {
            throw new UnauthorizedException("Unauthorized Access - try to login/register first");
        }

        // Token expired
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Unauthorized Access - try to login/register first");
        }

        // Extract email from JWT
        String email = jwtUtil.extractEmail(refreshTokenString);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Unauthorized Access - try to login/register first")
                );

        // Validate JWT
        if (!jwtUtil.isTokenValid(refreshTokenString, user.getEmail())) {
            throw new UnauthorizedException("Unauthorized Access - try to login/register first");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail());

        // Generate new refresh token
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // Delete old refresh token
        refreshTokenRepository.deleteByUser(user);

        // Save new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(newRefreshToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        // Response DTO
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);

        return response;
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {

        String refreshTokenString = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenString)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Unauthorized Access - try to login/register first"
                        )
                );

        // Delete refresh token
        refreshTokenRepository.deleteById(refreshToken.getId());
    }




}