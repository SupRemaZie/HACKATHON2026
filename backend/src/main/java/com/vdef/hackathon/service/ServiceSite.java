package com.vdef.hackathon.service;

import com.vdef.hackathon.jpa.SiteJPA;
import org.springframework.stereotype.Service;

import com.vdef.hackathon.repository.*;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceSite
{
    private final SiteRepository repositorySites;

    public ServiceSite(SiteRepository repositorySites)
    {
        this.repositorySites = repositorySites;
    }

    public List<SiteJPA> getSites()
    {
        return repositorySites.findAll();
    }
}
