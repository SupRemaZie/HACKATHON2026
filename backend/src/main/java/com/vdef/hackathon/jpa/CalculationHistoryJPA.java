package com.vdef.hackathon.jpa;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "calculation_history")
public class CalculationHistoryJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "co2_total_kg")
    private Double co2TotalKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private SiteJPA site;

    // getters and setters
    public CalculationHistoryJPA(SiteJPA site, Double co2TotalKg) {
        this.site = site;
        this.co2TotalKg = co2TotalKg;
    }
}