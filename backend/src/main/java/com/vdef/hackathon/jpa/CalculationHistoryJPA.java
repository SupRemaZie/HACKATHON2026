package com.vdef.hackathon.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "calculation_history")
public class CalculationHistoryJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private SiteJPA site;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "co2_total_kg", nullable = false)
    private Double co2TotalKg;

    @Column(name = "co2_construction_kg", nullable = false)
    private Double co2ConstructionKg;

    @Column(name = "co2_energy_kg", nullable = false)
    private Double co2EnergyKg;

    @Column(name = "co2_parking_kg", nullable = false)
    private Double co2ParkingKg;

    public CalculationHistoryJPA() {}

    public CalculationHistoryJPA(SiteJPA site, Double co2TotalKg) {
        this.site = site;
        this.co2TotalKg = co2TotalKg;
    }

    public Long getId() { return id; }
    public SiteJPA getSite() { return site; }
    public Integer getYear() { return year; }
    public Integer getMonth() { return month; }
    public Double getCo2TotalKg() { return co2TotalKg; }
    public Double getCo2ConstructionKg() { return co2ConstructionKg; }
    public Double getCo2EnergyKg() { return co2EnergyKg; }
    public Double getCo2ParkingKg() { return co2ParkingKg; }
}
