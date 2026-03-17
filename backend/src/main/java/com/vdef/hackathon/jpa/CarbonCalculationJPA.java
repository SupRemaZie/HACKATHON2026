package com.vdef.hackathon.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "carbon_calculations")
public class CarbonCalculationJPA {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private SiteJPA site;

    @Column(name = "co2_construction_kg", nullable = false)
    private Double co2ConstructionKg;

    @Column(name = "co2_energy_kg", nullable = false)
    private Double co2EnergyKg;

    @Column(name = "co2_parking_kg", nullable = false)
    private Double co2ParkingKg;

    @Column(name = "co2_total_kg", nullable = false)
    private Double co2TotalKg;

    @Column(name = "co2_per_m2", nullable = false)
    private Double co2PerM2;

    @Column(name = "co2_per_employee", nullable = false)
    private Double co2PerEmployee;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public CarbonCalculationJPA() {}

    public UUID getId() { return id; }
    public SiteJPA getSite() { return site; }
    public Double getCo2ConstructionKg() { return co2ConstructionKg; }
    public Double getCo2EnergyKg() { return co2EnergyKg; }
    public Double getCo2ParkingKg() { return co2ParkingKg; }
    public Double getCo2TotalKg() { return co2TotalKg; }
    public Double getCo2PerM2() { return co2PerM2; }
    public Double getCo2PerEmployee() { return co2PerEmployee; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
}
