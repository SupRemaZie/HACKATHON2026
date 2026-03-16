package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.site.CreateSiteRequest;
import com.vdef.hackathon.dto.site.SiteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sites")
@Tag(name = "Sites", description = "Gestion des sites physiques")
@SecurityRequirement(name = "bearer-jwt")
public class SitesController {

    @GetMapping
    @Operation(
            summary = "Lister tous les sites de l'utilisateur",
            description = "Récupère la liste de tous les sites appartenant à l'utilisateur connecté"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des sites",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<List<SiteResponse>> listSites() {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @Operation(
            summary = "Créer un nouveau site",
            description = "Crée un nouveau site pour l'utilisateur connecté"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Site créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<SiteResponse> createSite(@Valid @RequestBody CreateSiteRequest request) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer les détails d'un site",
            description = "Obtient les informations complètes d'un site spécifique"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détails du site",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<SiteResponse> getSite(
            @Parameter(description = "ID du site", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Modifier un site",
            description = "Met à jour les informations d'un site existant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Site modifié avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<SiteResponse> updateSite(
            @Parameter(description = "ID du site")
            @PathVariable UUID id,
            @Valid @RequestBody CreateSiteRequest request
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un site",
            description = "Supprime un site et toutes ses données associées"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Site supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<Void> deleteSite(
            @Parameter(description = "ID du site")
            @PathVariable UUID id
    ) {
        // À implémenter
        return ResponseEntity.noContent().build();
    }
}
