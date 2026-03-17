package com.vdef.hackathon.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requete de rafraichissement de token")
public record RefreshTokenRequestDTO(
        @Schema(description = "Refresh token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
        @NotBlank
        String refreshToken
) {}
