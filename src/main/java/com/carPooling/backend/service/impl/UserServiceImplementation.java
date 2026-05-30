package com.carPooling.backend.service.impl;

import com.carPooling.backend.dto.request.ProfileRequest;
import com.carPooling.backend.dto.response.UpdateProfileResponse;
import com.carPooling.backend.entity.User;
import com.carPooling.backend.exception.custom_exception.ConflictException;
import com.carPooling.backend.exception.custom_exception.UnauthorizedException;
import com.carPooling.backend.repository.UserRepository;
import com.carPooling.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j                          // ← Lombok generates: private static final Logger log = ... Simple Logging Facade
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(ProfileRequest req) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        log.debug("Updating profile for user asdfghjkl: {}", email);

        Optional<User> user = userRepository.findByEmail(email);

        log.debug("______________________________________________");

        if (user.isEmpty() || !user.get().getEmail().equals(req.getEmail())) {
            log.error("User not found with email: {}", email);
            throw new UnauthorizedException("Unauthorized: User not found");
        }

        User existingUser = user.get();

        if(req.getPhoneNumber() != null && !req.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
            if(userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
                log.error("Conflict: Phone number {} is already registered for another user", req.getPhoneNumber());
                throw new ConflictException("Phone number is already registered.");
            }
        }

        existingUser.setName(req.getName());
        existingUser.setDob(req.getDob());
        existingUser.setGender(req.getGender());
        existingUser.setProfilePicture(req.getProfilePicture());
        existingUser.setPhoneNumber(req.getPhoneNumber());
        existingUser.setCollegeCompanyName(req.getCollegeCompanyName());
        existingUser.setEmergencyContactName(req.getEmergencyContactName());
        existingUser.setEmergencyContactNumber(req.getEmergencyContactNumber());

        try {
            userRepository.save(existingUser);
        }catch (Exception e) {
            log.error("Unexpected error updating profile for {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to update profile. Please try again later.");
        }

        return new UpdateProfileResponse(
                existingUser.getName(),
                existingUser.getEmail(),
                existingUser.getPhoneNumber(),
                existingUser.getGender().name(),
                existingUser.getProfilePicture(),
                existingUser.getDob(),
                existingUser.getCollegeCompanyName(),
                existingUser.getEmergencyContactName(),
                existingUser.getEmergencyContactNumber()
        );
    }
}
