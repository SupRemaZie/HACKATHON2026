package com.vdef.hackathon.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.vdef.hackathon.service.JwtService;
import com.vdef.hackathon.web.dto.LoginRequestDTO;
import com.vdef.hackathon.web.dto.LoginResponseDTO;
import com.vdef.hackathon.web.dto.RefreshTokenRequestDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final JwtService jwtService;
    private final String defaultEmail;
    private final String defaultPassword;
    private final long expirationMillis;
    private final long refreshExpirationMillis;

    public AuthController(
        JwtService jwtService,
        @Value("${APP_AUTH_DEFAULT_EMAIL:admin@carbontrack.local}") String defaultEmail,
        @Value("${APP_AUTH_DEFAULT_PASSWORD:admin123}") String defaultPassword,
        @Value("${JWT_EXPIRATION_MS:3600000}") long expirationMillis,
        @Value("${JWT_REFRESH_EXPIRATION_MS:604800000}") long refreshExpirationMillis
    ) {
        this.jwtService = jwtService;
        this.defaultEmail = defaultEmail;
        this.defaultPassword = defaultPassword;
        this.expirationMillis = expirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        boolean isValid = defaultEmail.equalsIgnoreCase(request.email())
            && defaultPassword.equals(request.password());

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(java.util.Map.of("message", "Invalid email or password"));
        }

        String token = jwtService.generateToken(request.email(), expirationMillis);
        String refreshToken = jwtService.generateRefreshToken(request.email(), refreshExpirationMillis);

        LoginResponseDTO response = new LoginResponseDTO(
            token,
            "Bearer",
            expirationMillis / 1000,
            request.email(),
            refreshToken,
            refreshExpirationMillis / 1000
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            String email = jwtService.extractEmailFromRefreshToken(request.refreshToken());
            String token = jwtService.generateToken(email, expirationMillis);

            LoginResponseDTO response = new LoginResponseDTO(
                token,
                "Bearer",
                expirationMillis / 1000,
                email,
                request.refreshToken(),
                refreshExpirationMillis / 1000
            );

            return ResponseEntity.ok(response);
        } catch (JWTVerificationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(java.util.Map.of("message", "Invalid or expired refresh token"));
        }
    }
}
