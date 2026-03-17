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

@RestController
@RequestMapping("/api/sites")
@Tag(name = "Calculs CO₂", description = "Calcul et historique des émissions carbone")
@SecurityRequirement(name = "bearer-jwt")
public class CarbonCalculationController {

    @PostMapping("/{token}/calculate")
    @Operation(summary = "Calculer l'empreinte carbone d'un site")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Calcul effectué",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarbonCalculationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<CarbonCalculationResponse> calculateCarbon(
            @Parameter(description = "Token du site") @PathVariable String token) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{token}/calculations")
    @Operation(summary = "Récupérer l'historique des calculs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique des calculs",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarbonCalculationResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<List<CarbonCalculationResponse>> getCalculations(
            @Parameter(description = "Token du site") @PathVariable String token) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{token}/latest")
    @Operation(summary = "Récupérer le dernier calcul")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dernier calcul",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarbonCalculationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site ou calcul non trouvé")
    })
    public ResponseEntity<CarbonCalculationResponse> getLatestCalculation(
            @Parameter(description = "Token du site") @PathVariable String token) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{token}/history")
    @Operation(summary = "Récupérer l'historique d'évolution")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courbe d'évolution",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculationHistoryResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<List<CalculationHistoryResponse>> getHistory(
            @Parameter(description = "Token du site") @PathVariable String token) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{token}/history/{year}")
    @Operation(summary = "Récupérer le snapshot d'une année")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Données de l'année",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculationHistoryResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Site ou données non trouvées")
    })
    public ResponseEntity<List<CalculationHistoryResponse>> getHistoryByYear(
            @Parameter(description = "Token du site") @PathVariable String token,
            @Parameter(description = "Année", example = "2025") @PathVariable Integer year) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/compare")
    @Operation(summary = "Comparer plusieurs sites")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comparaison des sites",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteComparisonResponse[].class))),
            @ApiResponse(responseCode = "400", description = "Paramètre 'tokens' manquant ou invalide"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Un ou plusieurs sites non trouvés")
    })
    public ResponseEntity<List<SiteComparisonResponse>> compareSites(
            @Parameter(description = "Tokens séparés par des virgules (ex: rn001,pa001)")
            @RequestParam String tokens) {
        return ResponseEntity.ok().build();
    }
}
