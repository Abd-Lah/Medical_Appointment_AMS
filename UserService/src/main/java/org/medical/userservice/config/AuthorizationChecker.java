package org.medical.userservice.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("authorizationChecker")
public class AuthorizationChecker {

    public boolean isOwner(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            String claimId = jwt.getClaim("userId");
            return userId != null && userId.equals(claimId);
        }
        return false;
    }
}


