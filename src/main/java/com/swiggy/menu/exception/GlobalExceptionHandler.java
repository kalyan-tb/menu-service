package com.swiggy.menu.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        List<String> details = new ArrayList<>();
        details.add("Request body is not readable: " + ex.getMessage());

        ApiError apiError = buildApiErrorFromValidationError(
                ValidationError.REQUEST_BODY_NOT_READABLE, details);

        log.error("Message not readable: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add("Unexpected error: " + ex.getMessage());

        ApiError apiError = buildApiErrorFromValidationError(
                ValidationError.RESOURCE_NOT_FOUND, details);

        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    private ApiError buildApiErrorFromValidationError(ValidationError error, List<String> details) {
        return ApiError.builder()
                .errorStatus(error.errorStatus)
                .errorCode(error.errorCode)
                .errorMessage(error.errorMessage)
                .errorType(error.errorType)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
    }

}
