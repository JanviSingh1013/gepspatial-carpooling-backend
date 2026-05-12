package com.carPooling.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private List<String> messages;
    private LocalDateTime timestamp;
}