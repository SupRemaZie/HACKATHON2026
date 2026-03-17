package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.site.CreateSiteRequest;
import com.vdef.hackathon.jpa.EmissionFactorJPA;
import com.vdef.hackathon.jpa.SiteJPA;
import com.vdef.hackathon.jpa.SiteMaterialJPA;
import com.vdef.hackathon.repository.EmissionFactorRepository;
import com.vdef.hackathon.repository.SiteMaterialRepository;
import com.vdef.hackathon.repository.SiteRepository;
import org.springframework.web.bind.annotation.*;
import com.vdef.hackathon.service.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SiteController {

    private final ServiceSite siteService;
    private final SiteRepository siteRepository;
    private final SiteMaterialRepository siteMaterialRepository;

    public SiteController(ServiceSite siteService, SiteRepository siteRepository, SiteMaterialRepository siteMaterialRepository) {
        this.siteService = siteService;
        this.siteRepository = siteRepository;
        this.siteMaterialRepository = siteMaterialRepository;
    }

    @PostMapping("/site/create")
    public String createSite(@RequestBody CreateSiteRequest request)
    {
        String randomToken =  new java.util.Random()
            .ints(5, 0, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length())
            .mapToObj(i -> String.valueOf("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(i)))
            .collect(java.util.stream.Collectors.joining());

        System.out.println(randomToken);

        SiteJPA site = new SiteJPA();
        site.setName(request.name());
        site.setSurfaceM2(request.surfaceM2() != null ? request.surfaceM2().doubleValue() : null);
        site.setNbEmployees(request.nbEmployees() != null ? request.nbEmployees() : 0);
        site.setNbWorkstations(request.nbWorkstations() != null ? request.nbWorkstations() : 0);
        site.setParkingUnderground(request.parkingUnderground() != null ? request.parkingUnderground() : 0);
        site.setParkingOutdoor(request.parkingOutdoor() != null ? request.parkingOutdoor() : 0);
        site.setParkingBasement(request.parkingBasement() != null ? request.parkingBasement() : 0);
        site.setName("Capgemini EXAMPLE");
        site.setToken(randomToken);
        site.setCreatedBy(1L);
        Long siteId = siteRepository.save(site).getId();
        
        SiteMaterialJPA siteMaterial = new SiteMaterialJPA();
        siteMaterial.setSiteId(siteId);
        siteMaterial.setEmissionFactorId(Long.valueOf(request.emissionId()));
        siteMaterial.setQuantityKg(123.45);
        siteMaterial.setCo2KgStored((double) 0);
        siteMaterialRepository.save(siteMaterial);

        return randomToken;
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
