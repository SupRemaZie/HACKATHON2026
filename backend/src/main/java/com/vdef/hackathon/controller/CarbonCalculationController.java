package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.carbon.CarbonCalculationResponse;
import com.vdef.hackathon.dto.carbon.CalculationHistoryResponse;
import com.vdef.hackathon.dto.carbon.SiteComparisonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sites")
@Tag(name = "Calculs CO₂", description = "Calcul et historique des émissions carbone")
@SecurityRequirement(name = "bearer-jwt")
public class CarbonCalculationController {

    @PostMapping("/{id}/calculate")
    @Operation(
            summary = "Calculer l'empreinte carbone d'un site",
            description = "Déclenche le calcul d'émissions CO₂ pour un site (construction + énergie + parking) et l'enregistre en base"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Calcul effectué",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarbonCalculationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<CarbonCalculationResponse> calculateCarbon(
            @Parameter(description = "ID du site")
            @PathVariable UUID id
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/calculations")
    @Operation(
            summary = "Récupérer l'historique des calculs",
            description = "Obtient la liste complète des calculs effectués pour un site"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique des calculs",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarbonCalculationResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<List<CarbonCalculationResponse>> getCalculations(
            @Parameter(description = "ID du site")
            @PathVariable UUID id
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/latest")
    @Operation(
            summary = "Récupérer le dernier calcul",
            description = "Obtient le calcul carbone le plus récent pour un site"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dernier calcul",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarbonCalculationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site ou calcul non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<CarbonCalculationResponse> getLatestCalculation(
            @Parameter(description = "ID du site")
            @PathVariable UUID id
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/history")
    @Operation(
            summary = "Récupérer l'historique d'évolution",
            description = "Obtient la courbe d'évolution du CO₂ au fil du temps (données historisées)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courves d'évolution",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculationHistoryResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<List<CalculationHistoryResponse>> getHistory(
            @Parameter(description = "ID du site")
            @PathVariable UUID id
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/history/{year}")
    @Operation(
            summary = "Récupérer le snapshot d'une année",
            description = "Obtient les données historisées pour une année spécifique"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Données de l'année",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculationHistoryResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site ou données non trouvées"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<List<CalculationHistoryResponse>> getHistoryByYear(
            @Parameter(description = "ID du site")
            @PathVariable UUID id,
            @Parameter(description = "Année", example = "2025")
            @PathVariable Integer year
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @GetMapping("/compare")
    @Operation(
            summary = "Comparer plusieurs sites",
            description = "Compare 2 ou plus de sites côte à côte (Palier 3)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comparaison des sites",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteComparisonResponse[].class))),
            @ApiResponse(responseCode = "400", description = "Paramètre 'ids' manquant ou invalide"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Un ou plusieurs sites non trouvés"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<List<SiteComparisonResponse>> compareSites(
            @Parameter(description = "IDs séparés par des virgules (ex: id1,id2,id3)")
            @RequestParam String ids
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }
}
