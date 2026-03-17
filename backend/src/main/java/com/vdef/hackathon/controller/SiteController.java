package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.site.CreateSiteRequest;
import com.vdef.hackathon.jpa.SiteJPA;
import com.vdef.hackathon.repository.SiteRepository;
import org.springframework.web.bind.annotation.*;
import com.vdef.hackathon.service.*;

import java.util.List;
import java.util.Optional;

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
        site.setName(request.name());
        site.setSurfaceM2(request.surfaceM2() != null ? request.surfaceM2().doubleValue() : null);
        site.setNbEmployees(request.nbEmployees() != null ? request.nbEmployees() : 0);
        site.setNbWorkstations(request.nbWorkstations() != null ? request.nbWorkstations() : 0);
        site.setParkingUnderground(request.parkingUnderground() != null ? request.parkingUnderground() : 0);
        site.setParkingOutdoor(request.parkingOutdoor() != null ? request.parkingOutdoor() : 0);
        site.setParkingBasement(request.parkingBasement() != null ? request.parkingBasement() : 0);
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