package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.CarbonCalculationJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CarbonCalculationRepository extends JpaRepository<CarbonCalculationJPA, UUID> {

    /**
     * Retourne le dernier calcul par site (calculatedAt MAX).
     */
    @Query("""
        SELECT cc FROM CarbonCalculationJPA cc
        WHERE cc.calculatedAt = (
            SELECT MAX(cc2.calculatedAt)
            FROM CarbonCalculationJPA cc2
            WHERE cc2.site.id = cc.site.id
        )
    """)
    List<CarbonCalculationJPA> findLatestPerSite();
}
