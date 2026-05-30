package com.carPooling.backend.controller;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.dto.request.ProfileRequest;
import com.carPooling.backend.dto.request.UpdateProfileRequest;
import com.carPooling.backend.dto.response.UpdateProfileResponse;
import com.carPooling.backend.dto.response.UserProfileResponse;
import com.carPooling.backend.entity.User;
import com.carPooling.backend.repository.UserRepository;
import com.carPooling.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        return ResponseEntity.ok(UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender().name())
                .profileImageBase64(user.getProfilePicture())
                .createdAt(user.getCreatedAt())
                .build());
    }

    @PutMapping("/profile")
    public ResponseEntity<GenricDTO<UpdateProfileResponse>> register(
            @Valid @RequestBody ProfileRequest request
    ) {
        log.debug("Updating profile for user: {}", request.toString());
        UpdateProfileResponse response = userService.updateProfile(request);

        log.debug(
                "Profile updated successfully for user: {}. Response: {}",
                request.getEmail(),
                response.toString()
        );
        return ResponseEntity.ok(
                new GenricDTO<>(
                        true,
                        "Profile updated successfully",
                        response
                )
                );

    }
}