package com.vdef.hackathon.dto.emission;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Facteur d'émission ADEME")
public record EmissionFactorResponse(
        @Schema(description = "ID unique du facteur")
        UUID id,

        @Schema(description = "Catégorie (construction, energy, parking)")
        String category,

        @Schema(description = "Nom du matériau/source")
        String materialName,

        @Schema(description = "Facteur d'émission")
        Float factorKgCo2PerKg,

        @Schema(description = "Unité (kg, kWh, place)")
        String unit,

        @Schema(description = "Source de données")
        String source,

        @Schema(description = "Année de référence")
        Integer year
) {}
