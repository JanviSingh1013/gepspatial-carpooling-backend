package com.carPooling.backend.utils;

import java.util.regex.Pattern;


import java.util.regex.Pattern;

public class ValidationUtil {

    // Email regex
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    // Indian mobile regex
    private static final String MOBILE_REGEX =
            "^[6-9]\\d{9}$";

    // Password regex
    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])" +        // at least 1 uppercase
                    "(?=.*[a-z])" +         // at least 1 lowercase
                    "(?=.*\\d)" +           // at least 1 digit
                    "(?=.*[@$!%*?&])" +     // at least 1 special character
                    "[A-Za-z\\d@$!%*?&]{8,}$"; // minimum 8 characters

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(EMAIL_REGEX);

    private static final Pattern MOBILE_PATTERN =
            Pattern.compile(MOBILE_REGEX);

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(PASSWORD_REGEX);

    // Private constructor
    private ValidationUtil() {
    }

    // Email validation
    public static boolean isValidEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return EMAIL_PATTERN
                .matcher(email)
                .matches();
    }

    // Mobile validation
    public static boolean isValidMobile(String mobile) {

        if (mobile == null || mobile.trim().isEmpty()) {
            return false;
        }

        return MOBILE_PATTERN
                .matcher(mobile)
                .matches();
    }

    // Password validation
    public static boolean isValidPassword(String password) {

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        return PASSWORD_PATTERN
                .matcher(password)
                .matches();
    }
}
