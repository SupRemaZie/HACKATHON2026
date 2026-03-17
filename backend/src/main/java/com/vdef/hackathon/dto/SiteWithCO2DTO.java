package com.vdef.hackathon.dto;

public class SiteWithCO2DTO {

    private Long id;
    private Double co2TotalKg;

    public SiteWithCO2DTO(Long id, Double co2TotalKg) {
        this.id = id;
        this.co2TotalKg = co2TotalKg;
    }

    public Long getId() { return id; }
    public Double getCo2TotalKg() { return co2TotalKg; }
}
