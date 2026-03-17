package com.vdef.hackathon.dto.auth;

import java.util.UUID;

public record UserSummaryDTO(
    UUID id,
    String email,
    String fullName,
    String role
) {
}
