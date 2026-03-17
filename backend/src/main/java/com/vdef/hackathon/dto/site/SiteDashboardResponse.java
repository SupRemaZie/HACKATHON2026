package com.vdef.hackathon.dto.site;

import com.vdef.hackathon.jpa.SiteMaterialJPA;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Données dashboard d'un site")
public record SiteDashboardResponse(
        @Schema(description = "Détails du site")
        SiteResponse site,
        @Schema(description = "Matériaux associés au site")
        List<SiteMaterialJPA> siteMaterials
) {}
