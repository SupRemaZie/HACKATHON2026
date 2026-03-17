package com.vdef.hackathon.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reponse de connexion avec access token et refresh token")
public record LoginResponseDTO(
        @Schema(description = "Token JWT d'acces", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Type de token", example = "Bearer")
        String type,

        @Schema(description = "Duree de vie du token d'acces (en secondes)", example = "3600")
        long expiresIn,

        @Schema(description = "Email de l'utilisateur", example = "user@example.com")
        String email,

        @Schema(description = "Refresh token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken,

        @Schema(description = "Duree de vie du refresh token (en secondes)", example = "604800")
        long refreshExpiresIn,

        @Schema(description = "Role de l'utilisateur", example = "USER")
        String role
) {}
