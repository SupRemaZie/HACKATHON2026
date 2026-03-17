package com.vdef.hackathon.repository;

import com.vdef.hackathon.dto.SiteWithCO2DTO;
import com.vdef.hackathon.jpa.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteCO2Repository extends JpaRepository<SiteJPA, UUID>
{
    @Query("""
        SELECT new com.vdef.hackathon.dto.SiteWithCO2DTO(
            s.id, c.co2TotalKg
        )
        FROM SiteJPA s
        LEFT JOIN CalculationHistoryJPA c ON c.site = s
        GROUP BY s.id, c.co2TotalKg
        ORDER BY c.co2TotalKg DESC
        LIMIT 5
    """)
    List<SiteWithCO2DTO> fetchSiteWithCalculation();
}
