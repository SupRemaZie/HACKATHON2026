package com.vdef.hackathon.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

public class SiteWithCO2DTO {


    private UUID id;
    private Double co2TotalKg;

    public SiteWithCO2DTO(UUID id, Double co2TotalKg) {
        this.id = id;
        this.co2TotalKg = co2TotalKg;
    }

    // Getters
    public UUID getId() { return id; }
    public Double getCo2TotalKg() { return co2TotalKg; }

    // getters (and setters if needed)
}