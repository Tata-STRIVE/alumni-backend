package com.striveconnect.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles specific exceptions thrown by services (e.g., NOT_FOUND, CONFLICT).
     * Returns a structured JSON error response.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                ex.getReason(),
                request.getDescription(false)
        );
        logger.warn("Client error: {} on path {}", ex.getReason(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * A catch-all handler for any other unhandled exceptions.
     * This prevents stack traces from being sent to the client and provides a generic error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected internal server error occurred.",
                request.getDescription(false)
        );
        // Log the full stack trace for debugging purposes
        logger.error("Unhandled exception occurred on path {}", request.getDescription(false), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
