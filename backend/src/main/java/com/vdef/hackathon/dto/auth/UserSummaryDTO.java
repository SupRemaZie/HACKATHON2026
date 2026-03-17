package com.vdef.hackathon.dto.auth;

public record UserSummaryDTO(
        Long id,
        String email,
        String fullName,
        String role
) {
}
