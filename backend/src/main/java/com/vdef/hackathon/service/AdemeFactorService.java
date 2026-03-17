package com.vdef.hackathon.service;

import com.vdef.hackathon.client.AdemeClient;
import com.vdef.hackathon.dto.AdemeLineDto;
import com.vdef.hackathon.jpa.EmissionFactorJPA;
import com.vdef.hackathon.repository.EmissionFactorRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdemeFactorService {

    private final EmissionFactorRepository repository;
    private final AdemeClient ademeClient;

    public AdemeFactorService(EmissionFactorRepository repository, AdemeClient ademeClient) {
        this.repository = repository;
        this.ademeClient = ademeClient;
    }

    @Cacheable(value = "ademe-factor", key = "#ademeId")
    public AdemeLineDto findById(String ademeId) {
        return repository.findByAdemeId(ademeId)
                .map(this::toDto)
                .orElseGet(() -> {
                    AdemeLineDto dto = ademeClient.findById(ademeId);
                    if (dto != null) {
                        saveIfAbsent(dto);
                    }
                    return dto;
                });
    }

    @Cacheable(value = "ademe-search", key = "#q + '_' + #size")
    public List<AdemeLineDto> search(String q, int size) {
        List<EmissionFactorJPA> local = repository.findByMaterialNameContainingIgnoreCase(q);
        if (!local.isEmpty()) {
            return local.stream().map(this::toDto).toList();
        }
        List<AdemeLineDto> results = ademeClient.search(q, size);
        results.forEach(this::saveIfAbsent);
        return results;
    }

    private void saveIfAbsent(AdemeLineDto dto) {
        if (dto.getNomFrancais() == null) return;
        int year = parseYear(dto.getAnneePublication());
        if (repository.existsByMaterialNameAndYear(dto.getNomFrancais(), year)) return;

        EmissionFactorJPA entity = new EmissionFactorJPA();
        entity.setAdemeId(dto.getId());
        entity.setCategory(dto.getCategorie() != null ? dto.getCategorie() : "ademe");
        entity.setMaterialName(dto.getNomFrancais());
        entity.setFactorKgCo2PerKg(dto.getTotalPosteNonDecompose() != null
                ? dto.getTotalPosteNonDecompose().floatValue() : 0f);
        entity.setUnit(dto.getUnite() != null ? dto.getUnite() : "kg");
        entity.setSource(dto.getSource() != null ? dto.getSource() : "ADEME");
        entity.setYear(year);
        repository.save(entity);
    }

    private AdemeLineDto toDto(EmissionFactorJPA e) {
        // Reconstruction d'un AdemeLineDto depuis la DB via réflexion champ par champ
        // On passe par l'API client pour garder le contrat de retour identique
        // Si ademe_id connu → tenter un findById frais (non caché ici, on retourne le DTO local)
        AdemeLineDto dto = new AdemeLineDto();
        dto.setId(e.getAdemeId());
        dto.setNomFrancais(e.getMaterialName());
        dto.setCategorie(e.getCategory());
        dto.setTotalPosteNonDecompose(e.getFactorKgCo2PerKg() != null ? e.getFactorKgCo2PerKg().doubleValue() : null);
        dto.setUnite(e.getUnit());
        dto.setSource(e.getSource());
        dto.setAnneePublication(String.valueOf(e.getYear()));
        return dto;
    }

    private int parseYear(String annee) {
        if (annee == null) return 2023;
        try {
            // Format ADEME peut être "2023" ou "01/01/2023"
            String cleaned = annee.replaceAll(".*(\\d{4}).*", "$1");
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 2023;
        }
    }
}
