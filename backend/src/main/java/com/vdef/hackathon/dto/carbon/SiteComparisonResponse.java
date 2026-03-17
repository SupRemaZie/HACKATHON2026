package com.vdef.hackathon.dto.carbon;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Résumé d'un site pour comparaison")
public record SiteComparisonResponse(
        @Schema(description = "Token du site") String siteToken,
        @Schema(description = "Nom du site") String siteName,
        @Schema(description = "Surface en m²") Float surfaceM2,
        @Schema(description = "Nombre d'employés") Integer nbEmployees,
        @Schema(description = "Total CO₂ en kg") Float co2TotalKg,
        @Schema(description = "CO₂ par m²") Float co2PerM2,
        @Schema(description = "CO₂ par employé") Float co2PerEmployee,
        @Schema(description = "Part construction en %") Float constructionPercent,
        @Schema(description = "Part énergie en %") Float energyPercent,
        @Schema(description = "Part parking en %") Float parkingPercent
) {}
