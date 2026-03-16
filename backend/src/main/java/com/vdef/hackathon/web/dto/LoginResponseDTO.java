package com.vdef.hackathon.web.dto;

public record LoginResponseDTO(
    String token,
    String tokenType,
    long expiresIn,
    String email,
    String refreshToken,
    long refreshExpiresIn
) {
}
