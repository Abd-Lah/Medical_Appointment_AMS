package org.medical.userservice.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private Object user;
    private String token;
    private String refreshToken;

    public AuthResponse(Object user, String token, String refreshToken) {
        this.token = token;
        this.user = user;
        this.refreshToken = refreshToken;
    }
}