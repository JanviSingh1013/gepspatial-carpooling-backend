package com.carPooling.backend.exception;

import com.carPooling.backend.dto.GenricDTO;
import com.carPooling.backend.utils.StringConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenricDTO<List<String>>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        GenricDTO<List<String>> response =
                new GenricDTO<>(
                        StringConstant.INVALID_REQUEST,
                        "credentials format are invalid",
                        errors
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenricDTO<Object>> handleException(
            Exception ex
    ) {

        GenricDTO<Object> response =
                new GenricDTO<>(
                        StringConstant.FAILED,
                        ex.getMessage(),
                        null
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}