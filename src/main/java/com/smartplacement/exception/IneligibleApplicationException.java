package com.smartplacement.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Thrown when a student attempts to submit an application for a job drive
 * for which they do not meet the mandatory criteria.
 */
public class IneligibleApplicationException extends ApiException {

    private final List<String> rejectionReasons;

    public IneligibleApplicationException(String message, List<String> rejectionReasons) {
        super(message, HttpStatus.BAD_REQUEST);
        this.rejectionReasons = rejectionReasons;
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }
}
