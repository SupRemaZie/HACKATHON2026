package com.vdef.hackathon.web;

import com.vdef.hackathon.dto.SiteWithCO2DTO;
import com.vdef.hackathon.service.ServiceSite;
import com.vdef.hackathon.service.SiteWithCO2Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.*;

import java.util.List;

@RestController
public class DashboardFetchController
{

    private final SiteWithCO2Service siteWithCO2Service;

    public DashboardFetchController(SiteWithCO2Service siteWithCO2Service) {
        this.siteWithCO2Service = siteWithCO2Service;
    }
    

    @GetMapping("/dashboard")
    public List<SiteWithCO2DTO> DashboardFetch()
    {
       List<SiteWithCO2DTO> data = this.siteWithCO2Service.getSitesWithCO2();
        return data;
    }
    
}