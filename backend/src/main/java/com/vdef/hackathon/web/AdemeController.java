package com.vdef.hackathon.web;

import com.vdef.hackathon.dto.AdemeLineDto;
import com.vdef.hackathon.service.AdemeFactorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/ademe")
public class AdemeController {

    private final AdemeFactorService ademeFactorService;

    public AdemeController(AdemeFactorService ademeFactorService) {
        this.ademeFactorService = ademeFactorService;
    }

    /**
     * GET /api/ademe/factors/{id}
     * Retourne depuis la DB si connu, sinon appelle l'API ADEME, stocke et retourne.
     */
    @GetMapping("/factors/{id}")
    public AdemeLineDto getById(@PathVariable String id) {
        AdemeLineDto result = ademeFactorService.findById(id);
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facteur introuvable : " + id);
        }
        return result;
    }

    /**
     * GET /api/ademe/factors?q=béton&size=10
     * Retourne depuis la DB si des résultats existent, sinon appelle l'API ADEME, stocke et retourne.
     */
    @GetMapping("/factors")
    public List<AdemeLineDto> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int size) {
        return ademeFactorService.search(q, size);
    }
}
