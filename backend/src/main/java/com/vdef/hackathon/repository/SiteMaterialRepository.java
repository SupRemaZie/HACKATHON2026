package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.SiteMaterialJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteMaterialRepository extends JpaRepository<SiteMaterialJPA, Long>
{
    List<SiteMaterialJPA> findBySiteId(Long siteId);
}
