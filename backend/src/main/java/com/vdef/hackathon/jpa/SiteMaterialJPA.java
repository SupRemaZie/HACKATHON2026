package com.vdef.hackathon.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "site_materials")
public class SiteMaterialJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(name = "emission_factor_id", nullable = false)
    private Long emissionFactorId;

    @Column(name = "quantity_kg", nullable = false)
    private Double quantityKg;

    @Column(name = "co2_kg", nullable = false)
    private Double co2KgStored = 0.0;

    public SiteMaterialJPA() {}

    public Long getId() { return id; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Long getEmissionFactorId() { return emissionFactorId; }
    public void setEmissionFactorId(Long emissionFactorId) { this.emissionFactorId = emissionFactorId; }
    public Double getQuantityKg() { return quantityKg; }
    public void setQuantityKg(Double quantityKg) { this.quantityKg = quantityKg; }
    public Double getCo2KgStored() { return co2KgStored; }
    public void setCo2KgStored(Double co2KgStored) { this.co2KgStored = co2KgStored; }
}
