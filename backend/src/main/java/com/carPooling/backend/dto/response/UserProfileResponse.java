package com.carPooling.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String gender;

    private String roleType;

    private String profileImageBase64;

    private LocalDateTime createdAt;
}