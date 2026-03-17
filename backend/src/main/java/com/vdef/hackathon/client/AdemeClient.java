package com.vdef.hackathon.client;

import com.vdef.hackathon.dto.AdemeLineDto;
import com.vdef.hackathon.dto.AdemePageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class AdemeClient {

    private final RestClient restClient;
    private final String baseUrl;

    public AdemeClient(@Value("${ademe.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Recherche par ID ADEME exact.
     * Exemple id : "K9ThCcjH4MzGRTkTJt9yq"
     */
    public AdemeLineDto findById(String id) {
        AdemePageDto page = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/lines")
                        .queryParam("qs", "_id:\"" + id + "\"")
                        .build())
                .retrieve()
                .body(AdemePageDto.class);

        if (page == null || page.getResults() == null || page.getResults().isEmpty()) {
            return null;
        }
        return page.getResults().get(0);
    }

    /**
     * Recherche full-text sur le nom français.
     * Exemple q : "béton"
     */
    public List<AdemeLineDto> search(String q, int size) {
        AdemePageDto page = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/lines")
                        .queryParam("q", q)
                        .queryParam("q_fields", "Nom_base_français")
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(AdemePageDto.class);

        if (page == null || page.getResults() == null) {
            return List.of();
        }
        return page.getResults();
    }
}
