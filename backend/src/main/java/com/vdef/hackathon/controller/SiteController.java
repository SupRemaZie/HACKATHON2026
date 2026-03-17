package com.vdef.hackathon.controller;

import com.vdef.hackathon.jpa.SiteJPA;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.vdef.hackathon.service.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class SiteController {

    private final ServiceSite siteService;

    public SiteController(ServiceSite siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/site/{uuid}")
    public Optional<SiteJPA> getSiteByUUID(@PathVariable UUID uuid)
    {
        return siteService.getSiteByUUID(uuid);
    }

    @GetMapping("/sites")
    public List<SiteJPA> getAllSites(@RequestParam int id) {
        return siteService.getSites();
    }
}