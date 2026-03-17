package com.vdef.hackathon.dto.site;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Réponse détail d'un site")
public record SiteResponse(
        @Schema(description = "ID interne du site")
        Long id,

        @Schema(description = "Token court pour les routes front (ex: rn001)")
        String token,

        @Schema(description = "Nom du site")
        String name,

        @Schema(description = "Adresse du site")
        String address,

        @Schema(description = "Ville du site")
        String city,

        @Schema(description = "Surface totale en m²")
        Double surfaceM2,

        @Schema(description = "Nombre d'employés")
        Integer nbEmployees,

        @Schema(description = "Nombre de postes de travail")
        Integer nbWorkstations,

        @Schema(description = "Places de parking sous-dalle")
        Integer parkingUnderground,

        @Schema(description = "Places de parking sous-sol")
        Integer parkingBasement,

        @Schema(description = "Places de parking aériens")
        Integer parkingOutdoor,

        @Schema(description = "Date de création")
        LocalDateTime createdAt,

        @Schema(description = "Date de dernière modification")
        LocalDateTime updatedAt
) {}
