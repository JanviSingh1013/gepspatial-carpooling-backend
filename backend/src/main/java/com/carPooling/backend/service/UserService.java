package com.carPooling.backend.service;

import com.carPooling.backend.dto.request.UpdateProfileRequest;
import com.carPooling.backend.dto.response.UpdateProfileResponse;

public interface UserService {
    UpdateProfileResponse updateProfile(UpdateProfileRequest req);
}
