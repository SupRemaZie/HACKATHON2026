package com.vdef.hackathon.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "emission_factors")
public class EmissionFactorJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ademe_id", length = 100)
    private String ademeId;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "material_name", nullable = false, length = 255)
    private String materialName;

    @Column(name = "factor_kg_co2_per_kg", nullable = false)
    private Float factorKgCo2PerKg;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(nullable = false, length = 255)
    private String source;

    @Column(nullable = false)
    private Integer year;

    public EmissionFactorJPA() {}

    public Long getId() { return id; }
    public String getAdemeId() { return ademeId; }
    public void setAdemeId(String ademeId) { this.ademeId = ademeId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public Float getFactorKgCo2PerKg() { return factorKgCo2PerKg; }
    public void setFactorKgCo2PerKg(Float f) { this.factorKgCo2PerKg = f; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
