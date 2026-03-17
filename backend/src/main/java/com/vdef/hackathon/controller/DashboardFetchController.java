package com.vdef.hackathon.web;

import com.vdef.hackathon.dto.SiteWithCO2DTO;
import com.vdef.hackathon.dto.dashboard.DashboardKpiResponse;
import com.vdef.hackathon.service.DashboardKpiService;
import com.vdef.hackathon.service.SiteWithCO2Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardFetchController {

    private final SiteWithCO2Service siteWithCO2Service;
    private final DashboardKpiService dashboardKpiService;

    public DashboardFetchController(SiteWithCO2Service siteWithCO2Service,
                                     DashboardKpiService dashboardKpiService) {
        this.siteWithCO2Service = siteWithCO2Service;
        this.dashboardKpiService = dashboardKpiService;
    }

    @GetMapping
    public List<SiteWithCO2DTO> getSitesWithCO2() {
        return siteWithCO2Service.getSitesWithCO2();
    }

    @GetMapping("/kpis")
    public DashboardKpiResponse getKpis() {
        return dashboardKpiService.buildKpis();
    }
}