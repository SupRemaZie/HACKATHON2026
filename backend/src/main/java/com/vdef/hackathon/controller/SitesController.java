package com.vdef.hackathon.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vdef.hackathon.dto.site.CreateSiteRequest;
import com.vdef.hackathon.dto.site.SiteResponse;
import com.vdef.hackathon.jpa.SiteJPA;
import com.vdef.hackathon.repository.SiteRepository;
import com.vdef.hackathon.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sites")
@Tag(name = "Sites", description = "Gestion des sites physiques")
@SecurityRequirement(name = "bearer-jwt")
public class SitesController {

    private final SiteRepository siteRepository;
    private final TokenService tokenService;

    public SitesController(SiteRepository siteRepository, TokenService tokenService) {
        this.siteRepository = siteRepository;
        this.tokenService = tokenService;
    }

    @GetMapping
    @Operation(summary = "Lister les sites de l'utilisateur connecté")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des sites",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse[].class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<SiteResponse>> listSites() {
                Long userId = currentUserId();
                List<SiteResponse> sites = siteRepository.findByCreatedBy(userId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(sites);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau site")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Site créé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<SiteResponse> createSite(@Valid @RequestBody CreateSiteRequest request) {
        Long userId = currentUserId();

        SiteJPA site = new SiteJPA();
        site.setToken(tokenService.generateUniqueToken());
        site.setCreatedBy(userId);
        site.setName(request.siteName());
        site.setAddress(request.location());
        site.setCity(request.city());
        site.setSurfaceM2(request.areaM2() != null ? request.areaM2().doubleValue() : null);
        site.setNbEmployees(Objects.requireNonNullElse(request.employees(), 0));
        site.setNbWorkstations(Objects.requireNonNullElse(request.nbWorkstations(), 0));
        site.setParkingUnderground(Objects.requireNonNullElse(request.parkingUnderground(), 0));
        site.setParkingBasement(Objects.requireNonNullElse(request.parkingBasement(), 0));
        site.setParkingOutdoor(Objects.requireNonNullElse(request.parkingOutdoor(), 0));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(siteRepository.save(site)));
    }

    @GetMapping("/{token}")
    @Operation(summary = "Récupérer un site par son token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détails du site",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<SiteResponse> getSite(
            @Parameter(description = "Token du site", example = "rn001")
            @PathVariable String token) {
        SiteJPA site = findAndVerifyOwnership(token);
        return ResponseEntity.ok(toResponse(site));
    }

    @PutMapping("/{token}")
    @Operation(summary = "Modifier un site")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Site modifié",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<SiteResponse> updateSite(
            @Parameter(description = "Token du site") @PathVariable String token,
            @Valid @RequestBody CreateSiteRequest request) {
        SiteJPA site = findAndVerifyOwnership(token);

        site.setName(request.siteName());
        site.setAddress(request.location());
        site.setCity(request.city());
        if (request.areaM2() != null) site.setSurfaceM2(request.areaM2().doubleValue());
        if (request.employees() != null) site.setNbEmployees(request.employees());
        if (request.nbWorkstations() != null) site.setNbWorkstations(request.nbWorkstations());
        if (request.parkingUnderground() != null) site.setParkingUnderground(request.parkingUnderground());
        if (request.parkingBasement() != null) site.setParkingBasement(request.parkingBasement());
        if (request.parkingOutdoor() != null) site.setParkingOutdoor(request.parkingOutdoor());

        return ResponseEntity.ok(toResponse(siteRepository.save(site)));
    }

    @DeleteMapping("/{token}")
    @Operation(summary = "Supprimer un site")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Site supprimé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<Void> deleteSite(
            @Parameter(description = "Token du site") @PathVariable String token) {
        SiteJPA site = findAndVerifyOwnership(token);
        siteRepository.delete(site);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------

        private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié");
        }
                return (Long) auth.getPrincipal();
    }

    private SiteJPA findAndVerifyOwnership(String token) {
        SiteJPA site = siteRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Site introuvable : " + token));
        if (!site.getCreatedBy().equals(currentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return site;
    }

    private SiteResponse toResponse(SiteJPA s) {
        return new SiteResponse(
                s.getId(), s.getToken(), s.getName(), s.getAddress(), s.getCity(),
                s.getSurfaceM2(), s.getNbEmployees(), s.getNbWorkstations(),
                s.getParkingUnderground(), s.getParkingBasement(), s.getParkingOutdoor(),
                s.getCreatedAt(), s.getUpdatedAt()
        );
    }
}
