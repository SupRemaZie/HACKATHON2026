package com.vdef.hackathon.dto.carbon;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Entrée d'historique de calcul")
public record CalculationHistoryResponse(
        @Schema(description = "ID unique de l'entrée historique") Long id,
        @Schema(description = "Token du site concerné") String siteToken,
        @Schema(description = "Année de la mesure") Integer year,
        @Schema(description = "Total CO₂ enregistré en kg") Float co2TotalKg,
        @Schema(description = "Libellé (ex: 'Snapshot mensuel 2025')") String label,
        @Schema(description = "Date d'enregistrement") LocalDateTime recordedAt
) {}
