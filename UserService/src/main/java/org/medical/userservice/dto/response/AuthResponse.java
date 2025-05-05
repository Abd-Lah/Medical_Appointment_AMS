package org.medical.userservice.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private Object user;
    private String token;

    public AuthResponse(Object user, String token) {
        this.token = token;
        this.user = user;
    }
}