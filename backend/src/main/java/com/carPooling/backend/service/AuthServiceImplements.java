package com.carPooling.backend.service;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.AuthResponse;
import com.carPooling.backend.dto.response.CreatePasswordResponse;
import com.carPooling.backend.dto.response.LogInResponse;
import com.carPooling.backend.dto.response.RefreshTokenResponse;
import com.carPooling.backend.entity.RefreshToken;
import com.carPooling.backend.entity.User;
import com.carPooling.backend.exception.UserAlreadyExistsException;
import com.carPooling.backend.repository.RefreshTokenRepository;
import com.carPooling.backend.repository.UserRepository;
import com.carPooling.backend.security.JwtUtil;
import com.carPooling.backend.utils.StringConstant;
import com.carPooling.backend.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ValidationUtils;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImplements implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public GenricDTO<CreatePasswordResponse> createPassword(
            CreatePasswordRequest request
    ) {

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            return new GenricDTO<>(
                    StringConstant.INVALID_REQUEST,
                    "Password and confirm password do not match",
                    null
            );
        }

        Optional<User> optionalUser =
                userRepository.findByEmail(request.getEmail());

        User user;

        if (optionalUser.isEmpty()) {

            user = new User();
            user.setEmail(request.getEmail());

        } else {

            user = optionalUser.get();
        }

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        refreshTokenRepository.deleteByUser(user);

        String accessToken =
                jwtUtil.generateAccessToken(user.getEmail());

        String refreshTokenValue =
                jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        CreatePasswordResponse response =
                new CreatePasswordResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);

        return new GenricDTO<>(
                StringConstant.SUCCESS,
                "Password created successfully",
                response
        );
    }



    @Override
    @Transactional
    public GenricDTO<LogInResponse> login(LoginRequest req) {

        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        if(optionalUser.isEmpty()){
            return new GenricDTO<>(
                    StringConstant.UNAUTHORIZED,
                    "You haven't registered yet, please register first",
                    null
            );
        }

        User user = optionalUser.get();

        boolean isPasswordCorrect =
                passwordEncoder.matches(
                        req.getPassword(),
                        user.getPassword()
                );

        if(!isPasswordCorrect){
            return  new GenricDTO<>(
                    StringConstant.INVALID_REQUEST,
                    "Password is incorrect",
                    null
            );
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken =
                jwtUtil.generateAccessToken(user.getEmail());

        String refreshTokenValue =
                jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        LogInResponse response = new LogInResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);
        response.setUser(user);


        return new GenricDTO<>(
                StringConstant.SUCCESS,
                "Login successful",
                response
        );
    }


    @Override
    @Transactional
    public GenricDTO<RefreshTokenResponse> refreshToken(
            RefreshTokenRequest request
    ) {

        String refreshTokenString =
                request.getRefreshToken();

        Optional<RefreshToken> optionalRefreshToken =
                refreshTokenRepository.findByToken(
                        refreshTokenString
                );

        // Common unauthorized response
        GenricDTO<RefreshTokenResponse> unauthorizedResponse =
                new GenricDTO<>(
                        StringConstant.UNAUTHORIZED,
                        "Unauthorized Access - try to login/register first",
                        null
                );

        // Token not found
        if (optionalRefreshToken.isEmpty()) {
            return unauthorizedResponse;
        }

        RefreshToken storedToken =
                optionalRefreshToken.get();

        // Token revoked
        if (storedToken.isRevoked()) {
            return unauthorizedResponse;
        }

        // Token expired
        if (storedToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            return unauthorizedResponse;
        }

        // Extract email from JWT
        String email = jwtUtil.extractEmail(refreshTokenString);

        Optional<User> optionaluser = userRepository.findByEmail(email);
        if(optionaluser.isEmpty()){
            return unauthorizedResponse;
        }

        User user = optionaluser.get();


        // Validate JWT
        if (!jwtUtil.isTokenValid(refreshTokenString, user.getEmail())) {
            return unauthorizedResponse;
        }

        // Generate new access token
        // Generate new access token
        String newAccessToken =
                jwtUtil.generateAccessToken(user.getEmail());

        // Generate new refresh token
        String refreshTokenValue =
                jwtUtil.generateRefreshToken(user.getEmail());

        // Delete old refresh token
        refreshTokenRepository.deleteByUser(user);

        // Save new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        // Response
        RefreshTokenResponse response =
                new RefreshTokenResponse();

        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshTokenValue);

        return new GenricDTO<>(
                StringConstant.SUCCESS,
                "Token refreshed successfully",
                response
        );
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered");
        }

        if (userRepository.existsByPhoneNumber(
                req.getPhoneNumber())) {

            throw new UserAlreadyExistsException(
                    "Phone number already registered");
        }

        User user = User.builder()
                .name(req.getFullName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .password(passwordEncoder.encode(
                        req.getPassword()))
                .gender(req.getGender())
                .role(req.getRole())
                .profilePicture(req.getProfilePicture())
                .build();

        userRepository.save(user);

        // Generate Tokens
        String accessToken =
                jwtUtil.generateAccessToken(user.getEmail());

        String refreshTokenString =
                jwtUtil.generateRefreshToken(user.getEmail());

        // Save Refresh Token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getName())
                .roleType(user.getRole().name())
                .message("Registration successful!")
                .build();
    }





    @Override
    @Transactional
    public void logout(LogoutRequest request) {

        String refreshTokenString =
                request.getRefreshToken();

        RefreshToken token =
                refreshTokenRepository.findByToken(
                        refreshTokenString
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Refresh token not found"));

        token.setRevoked(true);

        refreshTokenRepository.save(token);
    }
}