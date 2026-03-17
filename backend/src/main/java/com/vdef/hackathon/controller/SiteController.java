package com.vdef.hackathon.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vdef.hackathon.dto.site.CreateSiteRequest;
import com.vdef.hackathon.jpa.SiteJPA;
import com.vdef.hackathon.repository.SiteRepository;
import com.vdef.hackathon.service.ServiceSite;

@RestController
public class SiteController {

    private final ServiceSite siteService;
    private final SiteRepository siteRepository;

    public SiteController(ServiceSite siteService, SiteRepository siteRepository) {
        this.siteService = siteService;
        this.siteRepository = siteRepository;
    }

    @PostMapping("/site/create")
    public String createSite(@RequestBody CreateSiteRequest request)
    {
        SiteJPA site = new SiteJPA();
        site.setName(request.siteName());
        site.setSurfaceM2(request.areaM2().doubleValue());
        site.setNbEmployees(request.employees());
        site.setNbWorkstations(request.nbWorkstations());
        site.setParkingUnderground(request.parkingSpots());
        site.setParkingOutdoor(request.parkingSpots());
        site.setParkingBasement(request.parkingSpots());
        site.setCreatedBy(1L);
        siteRepository.save(site);
        return "ok";
    }

    @GetMapping("/site/{id}")
    public Optional<SiteJPA> getSiteById(@PathVariable Long id)
    {
        return siteService.getSiteById(id);
    }

    @GetMapping("/sites")
    public List<SiteJPA> getAllSites() {
        return siteService.getSites();
    }
}