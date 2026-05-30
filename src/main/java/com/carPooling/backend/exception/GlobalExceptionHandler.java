package com.carPooling.backend.exception;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.exception.custom_exception.ConflictException;
import com.carPooling.backend.exception.custom_exception.InvalidRequestException;
import com.carPooling.backend.exception.custom_exception.InvalidTokenException;
import com.carPooling.backend.exception.custom_exception.UnauthorizedException;
import com.carPooling.backend.utils.StringConstant;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<GenricDTO<Void>> handleInvalidRequest(InvalidRequestException ex) {
        GenricDTO<Void> response = new GenricDTO<>();
        response.setStatus(false);
        response.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GenricDTO<Void>> handleUnauthorized(UnauthorizedException ex) {
        GenricDTO<Void> response = new GenricDTO<>();
        response.setStatus(false);
        response.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GenricDTO<Void>> invalidTokenException(InvalidTokenException ex) {
        GenricDTO<Void> response = new GenricDTO<>();
        response.setStatus(false);
        response.setError(StringConstant.REFRESH_TOKEN_EXPIRED);
        response.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<GenricDTO<Void>> handleConflict(ConflictException ex) {
        GenricDTO<Void> response = new GenricDTO<>();
        response.setStatus(false);
        response.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenricDTO<List<String>>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        GenricDTO<List<String>> response =
                new GenricDTO<>(
                        false,
                        "format is invalid",
                        errors
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenricDTO<Void>> handleGeneric(Exception ex) {

        log.debug(
                "An unexpected error occurred: {}",
                ex.getMessage(),
                ex
        );
        GenricDTO<Void> response = new GenricDTO<>();
        response.setStatus(false);
        response.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
