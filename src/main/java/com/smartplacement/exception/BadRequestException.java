package com.smartplacement.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a client request violates business constraints.
 * Mapped to HTTP 400 BAD_REQUEST.
 */
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
