package com.carPooling.backend.dto.request;

import com.carPooling.backend.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String imageUrl;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 200,
            message = "Name must be 2–200 characters")
    private String fullName;

    @NotBlank(message = "Date of birth is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "DOB must be in YYYY-MM-DD format"
    )
    private String dob;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Enter valid 10-digit Indian mobile number"
    )
    private String phoneNumber;

    @Size(
            min = 2,
            max = 150,
            message = "College name must be 2–150 characters"
    )
    private String collegeName;

    @Size(
            min = 2,
            max = 100,
            message = "Emergency contact name must be 2–100 characters"
    )
    private String emergencyContactName;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Enter valid emergency contact number"
    )
    private String emergencyContactNumber;
}