package com.vdef.hackathon.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requête d'inscription")
public record RegisterRequest(
        @Schema(description = "Email unique de l'utilisateur", example = "newuser@example.com")
        @Email
        @NotBlank
        String email,

        @Schema(description = "Mot de passe sécurisé", example = "MySecurePassword123!")
        @NotBlank
        String password,

        @Schema(description = "Nom complet de l'utilisateur", example = "Jean Dupont")
        @NotBlank
        String fullName
) {}
