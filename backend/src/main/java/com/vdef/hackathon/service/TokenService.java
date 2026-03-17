package com.vdef.hackathon.service;

import com.vdef.hackathon.jpa.SiteJPA;
import com.vdef.hackathon.repository.SiteRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final SiteRepository siteRepository;

    public TokenService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    /**
     * Génère un token unique de 8 caractères [a-z0-9] pour un site.
     * Réessaie jusqu'à trouver un token absent en base (collision quasi-impossible
     * avec 36^8 ≈ 2,8 billions de combinaisons, mais garanti quoi qu'il arrive).
     */
    public String generateUniqueToken() {
        String token;
        do {
            token = SiteJPA.generateRawToken();
        } while (siteRepository.findByToken(token).isPresent());
        return token;
    }
}
