package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.CalculationHistoryJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CalculationHistoryRepository extends JpaRepository<CalculationHistoryJPA, UUID> {

    /**
     * Somme du CO₂ total de tous les sites, groupée par (année, mois), triée chronologiquement.
     * Retourne Object[] { year (Integer), month (Integer), totalKg (Double) }
     */
    @Query("""
        SELECT ch.year, ch.month, SUM(ch.co2TotalKg)
        FROM CalculationHistoryJPA ch
        GROUP BY ch.year, ch.month
        ORDER BY ch.year, ch.month
    """)
    List<Object[]> findMonthlyTrend();
}
