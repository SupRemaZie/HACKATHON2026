package com.vdef.hackathon.service;

import com.vdef.hackathon.dto.dashboard.DashboardKpiResponse;
import com.vdef.hackathon.dto.dashboard.DashboardKpiResponse.*;
import com.vdef.hackathon.jpa.CarbonCalculationJPA;
import com.vdef.hackathon.repository.CarbonCalculationRepository;
import com.vdef.hackathon.repository.CalculationHistoryRepository;
import com.vdef.hackathon.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DashboardKpiService {

    private final CarbonCalculationRepository calcRepo;
    private final CalculationHistoryRepository historyRepo;
    private final SiteRepository siteRepo;

    public DashboardKpiService(CarbonCalculationRepository calcRepo,
                                CalculationHistoryRepository historyRepo,
                                SiteRepository siteRepo) {
        this.calcRepo = calcRepo;
        this.historyRepo = historyRepo;
        this.siteRepo = siteRepo;
    }

    public DashboardKpiResponse buildKpis() {
        List<CarbonCalculationJPA> latest = calcRepo.findLatestPerSite();

        double totalCo2 = latest.stream().mapToDouble(CarbonCalculationJPA::getCo2TotalKg).sum();
        double avgPerM2 = latest.stream().mapToDouble(CarbonCalculationJPA::getCo2PerM2).average().orElse(0);
        double avgPerEmployee = latest.stream().mapToDouble(CarbonCalculationJPA::getCo2PerEmployee).average().orElse(0);
        long siteCount = siteRepo.count();

        CarbonCalculationJPA top = latest.stream()
                .max(Comparator.comparingDouble(CarbonCalculationJPA::getCo2TotalKg)).orElse(null);
        CarbonCalculationJPA low = latest.stream()
                .min(Comparator.comparingDouble(CarbonCalculationJPA::getCo2TotalKg)).orElse(null);

        double constructionTotal = latest.stream().mapToDouble(CarbonCalculationJPA::getCo2ConstructionKg).sum();
        double energyTotal = latest.stream().mapToDouble(CarbonCalculationJPA::getCo2EnergyKg).sum();
        double parkingTotal = latest.stream().mapToDouble(CarbonCalculationJPA::getCo2ParkingKg).sum();

        List<TrendPoint> trend = historyRepo.findMonthlyTrend().stream()
                .map(row -> new TrendPoint(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).doubleValue()))
                .toList();

        return new DashboardKpiResponse(
                totalCo2,
                round(avgPerM2),
                round(avgPerEmployee),
                siteCount,
                top == null ? null : new SiteSummary(top.getSite().getId(), top.getSite().getToken(), top.getSite().getName(), top.getCo2TotalKg()),
                low == null ? null : new SiteSummary(low.getSite().getId(), low.getSite().getToken(), low.getSite().getName(), low.getCo2TotalKg()),
                new Co2Breakdown(constructionTotal, energyTotal, parkingTotal),
                trend
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
