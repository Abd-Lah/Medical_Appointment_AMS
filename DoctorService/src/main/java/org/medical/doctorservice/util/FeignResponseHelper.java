package org.medical.doctorservice.util;

import org.medical.doctorservice.exception.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

// A dedicated helper for Feign response validation
@Component
public class FeignResponseHelper {

    public void checkResponseStatus(ResponseEntity<?> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InvalidRequestException("You are not allowed to access this resource");
        }
    }
}