package com.carPooling.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatePasswordRequest extends BaseAuthRequest {

    @JsonProperty("confirm_password")
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}