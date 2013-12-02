package se.inera.statistics.web.util;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.web.service.ChartDataService;

public class HealthCheckUtil {

    private static final int NANOS_PER_MS = 1000000;

    @Autowired
    private ChartDataService chartDataService;

    public long getOverviewTime() {
        long startTime = System.nanoTime();
        chartDataService.getOverviewData();
        long doneTime = System.nanoTime();
        return (doneTime - startTime) / NANOS_PER_MS;
    }
}
