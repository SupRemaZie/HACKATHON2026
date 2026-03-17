package com.vdef.hackathon.service;

import com.vdef.hackathon.dto.SiteWithCO2DTO;
import com.vdef.hackathon.repository.SiteCO2Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteWithCO2Service {

    private final SiteCO2Repository siteCO2Repository;

    public SiteWithCO2Service(SiteCO2Repository siteCO2Repository) {
        this.siteCO2Repository = siteCO2Repository;
    }

    /**
     * Fetch all sites with their total CO2 from calculation history.
     * Sites without history will have null for co2TotalKg.
     */
    public List<SiteWithCO2DTO> getSitesWithCO2() {
        return siteCO2Repository.fetchSiteWithCalculation();
    }
}