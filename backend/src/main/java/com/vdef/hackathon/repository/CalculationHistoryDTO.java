package com.vdef.hackathon.repository;

import com.vdef.hackathon.dto.SiteDTO;
import jakarta.persistence.*;

public class CalculationHistoryDTO
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float co2_total_kg;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteDTO site_id;
}
