package org.medical.appointmentservice.exception;



public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
