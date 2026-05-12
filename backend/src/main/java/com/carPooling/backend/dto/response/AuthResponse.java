package com.carPooling.backend.dto.response;

import lombok.*;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private String email;
    private String fullName;
    private String roleType;
    private String message;
}