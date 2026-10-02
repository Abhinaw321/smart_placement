package com.smartplacement.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when file upload, validation, or filesystem I/O fails.
 */
public class FileStorageException extends ApiException {

    public FileStorageException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
