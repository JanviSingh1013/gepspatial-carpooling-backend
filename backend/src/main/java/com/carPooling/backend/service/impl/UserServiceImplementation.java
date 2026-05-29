package com.carPooling.backend.service.impl;

import com.carPooling.backend.dto.request.UpdateProfileRequest;
import com.carPooling.backend.dto.response.UpdateProfileResponse;
import com.carPooling.backend.entity.User;
import com.carPooling.backend.repository.UserRepository;
import com.carPooling.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j                          // ← Lombok generates: private static final Logger log = ... Simple Logging Facade
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    UserRepository userRepository;


    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(UpdateProfileRequest req) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        log.debug("Updating profile for user: {}", email);

        Optional<User> user = userRepository.findByEmail(email);


//        userRepository.save(user);

        UpdateProfileResponse response = new UpdateProfileResponse();
        return  response;
    }
}
