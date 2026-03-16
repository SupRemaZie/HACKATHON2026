package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.emission.EmissionFactorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emission-factors")
@Tag(name = "Facteurs d'émission", description = "Référentiel ADEME des facteurs d'émission CO₂")
public class EmissionFactorsController {

    @GetMapping
    @Operation(
            summary = "Récupérer tous les facteurs d'émission",
            description = "Récupère la liste complète des facteurs d'émission ADEME (construction, énergie, parking)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des facteurs",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmissionFactorResponse[].class))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<List<EmissionFactorResponse>> getAllFactors() {
        // À implémenter
        return ResponseEntity.ok().build();
    }

    @GetMapping(params = "category")
    @Operation(
            summary = "Récupérer les facteurs filtrés par catégorie",
            description = "Récupère les facteurs d'émission d'une catégorie spécifique (construction, energy, parking)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Facteurs filtrés",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmissionFactorResponse[].class))),
            @ApiResponse(responseCode = "400", description = "Catégorie invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<List<EmissionFactorResponse>> getFactorsByCategory(
            @Parameter(description = "Catégorie de filtre (construction, energy, parking)", example = "energy")
            @RequestParam String category
    ) {
        // À implémenter
        return ResponseEntity.ok().build();
    }
}
