package org.medical.patientservice.exception;


import org.springframework.http.ResponseEntity;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
