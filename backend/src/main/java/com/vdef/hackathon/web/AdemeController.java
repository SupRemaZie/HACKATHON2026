package com.vdef.hackathon.web;

import com.vdef.hackathon.client.AdemeClient;
import com.vdef.hackathon.dto.AdemeLineDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/ademe")
public class AdemeController {

    private final AdemeClient ademeClient;

    public AdemeController(AdemeClient ademeClient) {
        this.ademeClient = ademeClient;
    }

    /**
     * GET /api/ademe/factors/{id}
     * Récupère un facteur d'émission par son ID ADEME.
     */
    @GetMapping("/factors/{id}")
    public AdemeLineDto getById(@PathVariable String id) {
        AdemeLineDto result = ademeClient.findById(id);
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facteur introuvable : " + id);
        }
        return result;
    }

    /**
     * GET /api/ademe/factors?q=béton&size=10
     * Recherche des facteurs d'émission par nom.
     */
    @GetMapping("/factors")
    public List<AdemeLineDto> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int size) {
        return ademeClient.search(q, size);
    }
}
