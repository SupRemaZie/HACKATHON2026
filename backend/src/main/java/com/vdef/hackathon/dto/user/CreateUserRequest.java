package com.vdef.hackathon.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Requête de création/modification d'un utilisateur")

public record CreateUserRequest
(
        @Schema(description = "Email de l'utilisateur", example = "test@test.fr")
        @NotBlank
        String email,

        @Schema(description = "Mot de passe hashé+salté de l'utilisateur")
        String passwordHash,

        @Schema(description = "Nom complet de l'utilisateur", example = " John Doe")
        String fullName,

        @Schema(description = "Role", example = "USER")
        String role
) {}
