package com.carPooling.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends BaseAuthRequest {
}