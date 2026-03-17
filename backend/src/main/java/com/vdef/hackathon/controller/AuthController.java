package com.vdef.hackathon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vdef.hackathon.dto.auth.AuthResponse;
import com.vdef.hackathon.dto.auth.LoginRequestDTO;
import com.vdef.hackathon.dto.auth.LoginResponseDTO;
import com.vdef.hackathon.dto.auth.RefreshTokenRequestDTO;
import com.vdef.hackathon.dto.auth.RegisterRequest;
import com.vdef.hackathon.dto.auth.UserSummaryDTO;
import com.vdef.hackathon.jpa.UserJPA;
import com.vdef.hackathon.service.JwtService;
import com.vdef.hackathon.service.ServiceUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints d'authentification et d'inscription")
public class AuthController {
    private final JwtService jwtService;
    private final ServiceUser serviceUser;
    private final PasswordEncoder passwordEncoder;
    private final long expirationMillis;
    private final long refreshExpirationMillis;

    public AuthController(
        JwtService jwtService,
        ServiceUser serviceUser,
        PasswordEncoder passwordEncoder,
        @org.springframework.beans.factory.annotation.Value("${JWT_EXPIRATION_MS:3600000}") long expirationMillis,
        @org.springframework.beans.factory.annotation.Value("${JWT_REFRESH_EXPIRATION_MS:604800000}") long refreshExpirationMillis
    ) {
        this.jwtService = jwtService;
        this.serviceUser = serviceUser;
        this.passwordEncoder = passwordEncoder;
        this.expirationMillis = expirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> users() {
        return ResponseEntity.ok(serviceUser.getAllUsersSummary());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        UserJPA user = serviceUser.getUserByEmail(request.email()).orElse(null);

        boolean isValid = user != null
            && passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(java.util.Map.of("message", "Invalid email or password"));
        }

        String token = jwtService.generateToken(user.getEmail(), expirationMillis);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), refreshExpirationMillis);

        LoginResponseDTO response = new LoginResponseDTO(
            token,
            "Bearer",
            expirationMillis / 1000,
            user.getEmail(),
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
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(java.util.Map.of("message", "Invalid or expired refresh token"));
        }
    }

    @PostMapping("/register")
    @Operation(
            summary = "Créer un compte utilisateur",
            description = "Enregistre un nouvel utilisateur dans le système"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscription réussie, token JWT retourné",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (email déjà existant, validation échouée)"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

}
