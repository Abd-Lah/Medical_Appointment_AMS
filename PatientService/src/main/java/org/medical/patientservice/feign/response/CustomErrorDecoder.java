package org.medical.patientservice.feign.response;

import feign.Response;
import feign.codec.ErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.medical.patientservice.dto.response.ErrorResponse;
import org.medical.patientservice.exception.ValidationException;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 422) {
            try {
                // Extract the error response body as a string
                String responseBody = new String(response.body().asInputStream().readAllBytes());

                // Use ObjectMapper to parse the error response
                ObjectMapper objectMapper = new ObjectMapper();
                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);

                // Throw a ValidationException with the error message
                return new ValidationException(errorResponse.getMessage());
            } catch (Exception e) {
                return defaultErrorDecoder.decode(methodKey, response);
            }
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}

