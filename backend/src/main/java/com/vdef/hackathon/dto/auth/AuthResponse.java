package com.vdef.hackathon.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'authentification avec token JWT")
public record AuthResponse(
        @Schema(description = "Token JWT à utiliser en Bearer dans les requêtes", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Type de token", example = "Bearer")
        String type,

        @Schema(description = "Email de l'utilisateur connecté", example = "user@example.com")
        String email,

        @Schema(description = "Rôle de l'utilisateur", allOf = {}, example = "USER")
        String role
) {}
