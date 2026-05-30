package com.carPooling.backend.dto.response;

import com.carPooling.backend.entity.User;
import com.carPooling.backend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class UpdateProfileResponse {
    private String name;
    private String email;
    private String phoneNumber;
    private String gender;
    private String profilePicture;
    private LocalDate dob;
    private String collegeCompanyName;
    private String emergencyContactName;
    private String emergencyContactNumber;
}
