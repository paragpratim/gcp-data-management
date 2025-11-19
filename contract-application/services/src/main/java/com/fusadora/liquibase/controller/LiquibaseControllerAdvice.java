package com.fusadora.liquibase.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * com.fusadora.liquibase.controller.LiquibaseControllerAdvice
 * This class handles exceptions globally for the LiquibaseController.
 * It provides custom responses for different types of exceptions that may occur during request processing.
 *
 * @author Parag Ghosh
 * @since 17/11/2025
 */

@ControllerAdvice
public class LiquibaseControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(LiquibaseControllerAdvice.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidJson(HttpMessageNotReadableException e) {
        logger.error("Invalid JSON payload: {}", e.getMessage());
        return ResponseEntity.badRequest().body("Invalid JSON payload");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        logger.error("Validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("Invalid argument: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException e) {
        logger.error("Invalid number format: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body("Invalid contract ID format: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + e.getMessage());
    }
}
