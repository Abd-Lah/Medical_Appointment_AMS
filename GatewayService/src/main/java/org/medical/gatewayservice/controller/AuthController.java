package org.medical.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.gatewayservice.security.TokenBlacklist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenBlacklist tokenBlacklist;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            tokenBlacklist.blacklist(token);
        }
        return ResponseEntity.noContent().build();
    }
}


