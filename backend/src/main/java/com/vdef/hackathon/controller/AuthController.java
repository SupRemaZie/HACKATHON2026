package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.auth.LoginRequestDTO;
import com.vdef.hackathon.dto.auth.LoginResponseDTO;
import com.vdef.hackathon.dto.auth.RefreshTokenRequestDTO;
import com.vdef.hackathon.dto.auth.RegisterRequest;
import com.vdef.hackathon.jpa.UserJPA;
import com.vdef.hackathon.repository.UserRepository;
import com.vdef.hackathon.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints d'authentification et d'inscription")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final long expirationMillis;
    private final long refreshExpirationMillis;

    public AuthController(
        JwtService jwtService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Value("${jwt.expiration-ms:3600000}") long expirationMillis,
        @Value("${JWT_REFRESH_EXPIRATION_MS:604800000}") long refreshExpirationMillis
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.expirationMillis = expirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un compte utilisateur")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Email déjà utilisé"));
        }

        UserJPA user = new UserJPA();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), expirationMillis);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), refreshExpirationMillis);

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDTO(
            token, "Bearer", expirationMillis / 1000,
            user.getEmail(), refreshToken, refreshExpirationMillis / 1000
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        return userRepository.findByEmail(request.email())
            .filter(user -> passwordEncoder.matches(request.password(), user.getPasswordHash()))
            .map(user -> {
                String token = jwtService.generateToken(user.getEmail(), expirationMillis);
                String refreshToken = jwtService.generateRefreshToken(user.getEmail(), refreshExpirationMillis);
                return ResponseEntity.ok((Object) new LoginResponseDTO(
                    token, "Bearer", expirationMillis / 1000,
                    user.getEmail(), refreshToken, refreshExpirationMillis / 1000
                ));
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Email ou mot de passe incorrect")));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Renouveler le token d'accès")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            String email = jwtService.extractEmailFromRefreshToken(request.refreshToken());
            String token = jwtService.generateToken(email, expirationMillis);
            return ResponseEntity.ok(new LoginResponseDTO(
                token, "Bearer", expirationMillis / 1000,
                email, request.refreshToken(), refreshExpirationMillis / 1000
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Token invalide ou expiré"));
        }
    }
}
