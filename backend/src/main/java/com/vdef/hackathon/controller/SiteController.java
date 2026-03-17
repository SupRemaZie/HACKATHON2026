package com.vdef.hackathon.controller;

import com.vdef.hackathon.jpa.SiteJPA;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.vdef.hackathon.service.*;

import java.util.List;
import java.util.UUID;

@RestController
public class SiteController {

    private final ServiceSite siteService;

    public SiteController(ServiceSite siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/sites")
    public List<SiteJPA> getSideById(@RequestParam int id) {
        return siteService.getSites();
    }
}