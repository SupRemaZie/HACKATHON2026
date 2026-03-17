package com.vdef.hackathon.dto.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Requête de création/modification d'un site")
public record CreateSiteRequest(
        @Schema(description = "Nom du site", example = "Capgemini Rennes")
        @NotBlank
        String name,

        @Schema(description = "Adresse du site", example = "Avenue de Joinville")
        String address,

        @Schema(description = "Ville du site", example = "Rennes")
        String city,

        @Schema(description = "Surface totale en m²", example = "11771")
        @Positive
        Float surfaceM2,

        @Schema(description = "Nombre d'employés", example = "1800")
        Integer nbEmployees,

        @Schema(description = "Nombre de postes de travail", example = "1037")
        Integer nbWorkstations,

        @Schema(description = "Places de parking sous-dalle", example = "41")
        Integer parkingUnderground,

        @Schema(description = "Places de parking sous-sol", example = "184")
        Integer parkingBasement,

        @Schema(description = "Places de parking aériens", example = "83")
        Integer parkingOutdoor,

        @Schema(description = "Consommation annuelle en kWh", example = "1840000")
        @Positive
        Float eletricityMWh,

        @Schema(description = "Source d'énergie (electricity_fr, gaz_naturel, fioul)", example = "electricity_fr")
        String energySource,

        @Schema(description = "Emission ID", example = "1")
        Integer emissionId
) {}
