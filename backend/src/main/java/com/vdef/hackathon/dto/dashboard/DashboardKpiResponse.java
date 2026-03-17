package com.vdef.hackathon.dto.dashboard;

import java.util.List;

public record DashboardKpiResponse(
        double totalCo2Kg,
        double avgCo2PerM2,
        double avgCo2PerEmployee,
        long siteCount,
        SiteSummary topEmitter,
        SiteSummary lowestEmitter,
        Co2Breakdown breakdown,
        List<TrendPoint> trend
) {
    public record SiteSummary(Long id, String token, String name, double co2Kg) {}
    public record Co2Breakdown(double constructionKg, double energyKg, double parkingKg) {}
    public record TrendPoint(int year, int month, double totalKg) {}
}
