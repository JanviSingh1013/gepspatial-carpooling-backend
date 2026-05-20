package com.carPooling.backend.dto.response;

import com.carPooling.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInResponse extends BaseAuthResponse {
    private User user;
}
