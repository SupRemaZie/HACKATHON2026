package com.vdef.hackathon.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requete de connexion")
public record LoginRequestDTO(
        @Schema(description = "Email de l'utilisateur", example = "user@example.com")
        @Email
        @NotBlank
        String email,

        @Schema(description = "Mot de passe", example = "password123")
        @NotBlank
        String password
) {}
