package org.medical.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HMACSHA256"); // Create a SecretKey for HMACSHA256

        return NimbusJwtDecoder.withSecretKey(secretKeySpec).build();  // Use SecretKeySpec
    }

    @Bean
    public SecurityFilterChain SecurityWebFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/register").permitAll()  // Allow login and register without authentication
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")  // Only admin can access these routes
                        .requestMatchers("/doctor/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DOCTOR")  // Admin or Doctor
                        .requestMatchers("/patient/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PATIENT")  // Admin or Patient
                        .anyRequest().authenticated()  // Secure all other routes
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))  // Enable JWT-based authentication for the resource server
                .httpBasic(withDefaults());

        return http.build();
    }
}
