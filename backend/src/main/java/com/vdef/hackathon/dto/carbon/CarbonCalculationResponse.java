package com.vdef.hackathon.dto.carbon;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.JsonNode;

@Schema(description = "Réponse d'un calcul carbone")
public record CarbonCalculationResponse(
        @Schema(description = "ID unique du calcul") Long id,
        @Schema(description = "Token du site concerné") String siteToken,
        @Schema(description = "Émissions CO₂ de la construction en kg") Float co2ConstructionKg,
        @Schema(description = "Émissions CO₂ de l'énergie en kg") Float co2EnergyKg,
        @Schema(description = "Émissions CO₂ du parking en kg") Float co2ParkingKg,
        @Schema(description = "Émissions CO₂ totales en kg") Float co2TotalKg,
        @Schema(description = "Émissions CO₂ par m² en kg/m²") Float co2PerM2,
        @Schema(description = "Émissions CO₂ par employé en kg/pers") Float co2PerEmployee,
        @Schema(description = "Détail du calcul par matériau (JSON)") JsonNode breakdown,
        @Schema(description = "Date du calcul") LocalDateTime calculatedAt
) {}
