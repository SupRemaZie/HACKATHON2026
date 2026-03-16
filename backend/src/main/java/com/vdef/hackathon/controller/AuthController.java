package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.auth.LoginRequest;
import com.vdef.hackathon.dto.auth.RegisterRequest;
import com.vdef.hackathon.dto.auth.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints d'authentification et d'inscription")
public class AuthController {

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

    @PostMapping("/login")
    @Operation(
            summary = "Se connecter",
            description = "Authentifie un utilisateur et retourne un token JWT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // À implémenter
        return ResponseEntity.ok().build();
    }
}
