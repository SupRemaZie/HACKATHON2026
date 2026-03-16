package com.vdef.hackathon.repository;

import com.vdef.hackathon.dto.SiteDTO;
import com.vdef.hackathon.dto.SiteWithCO2DTO;
import com.vdef.hackathon.jpa.SiteJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/*import com.vdef.hackathon.dto.*;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteWithCO2Repository extends JpaRepository<SiteJPA, UUID>
{
    @Query("""
        SELECT new com.vdef.hackathon.dto.SiteWithCO2DTO(
            s.id,
            s.name,
            ch.co2TotalKg
        )
        FROM SiteJPA s
        LEFT JOIN CalculationHistoryJPA ch
        ON s.id = ch.site.id
    """)
    List<SiteWithCO2DTO> findSitesWithCo2();
}
*/