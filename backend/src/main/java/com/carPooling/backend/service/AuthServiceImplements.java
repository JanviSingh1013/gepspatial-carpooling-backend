package com.carPooling.backend.service;

import com.carPooling.backend.dto.request.*;
import com.carPooling.backend.dto.response.AuthResponse;
import com.carPooling.backend.entity.User;
import com.carPooling.backend.exception.UserAlreadyExistsException;
import com.carPooling.backend.repository.UserRepository;
import com.carPooling.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImplements implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        // 1. Check duplicate email
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        // 2. Check duplicate phone number
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new UserAlreadyExistsException("Phone number already registered");
        }

        // 3. Create user entity
        User user = User.builder()
                .name(req.getFullName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .password(passwordEncoder.encode(req.getPassword()))
                .gender(req.getGender())
                .role(req.getRole())
                .profilePicture(req.getProfilePicture())
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getName())
                .roleType(user.getRole().name())
                .message("Registration successful!")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest req) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getName())
                .roleType(user.getRole().name())
                .message("Login successful!")
                .build();
    }
}