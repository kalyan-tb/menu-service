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
