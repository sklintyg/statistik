package se.inera.statistics.web.util;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.web.service.ChartDataService;

public class HealthCheckUtil {

    private static final int NANOS_PER_MS = 1000000;

    @Autowired
    private ChartDataService chartDataService;

    @Autowired
    private HSAWebServiceCalls hsaService;

    public Status getOverviewStatus() {
        boolean ok;
        long startTime = System.nanoTime();
        try {
            chartDataService.getOverviewData();
            ok = true;
        } catch (Exception e) {
            ok = false;
        }
        long doneTime = System.nanoTime();
        return new Status((doneTime - startTime) / NANOS_PER_MS, ok);
    }

    public boolean isOverviewOk() {
        return getOverviewStatus().isOk();
    }

    public Status getHsaStatus() {
        boolean ok;
        long startTime = System.nanoTime();
        try {
            hsaService.callPing();
            ok = true;
        } catch (Exception e) {
            ok = false;
        }
        long doneTime = System.nanoTime();
        return new Status((doneTime - startTime) / NANOS_PER_MS, ok);
    }

    public static final class Status {
        private final long time;
        private final boolean ok;

        private Status(long time, boolean ok) {
            this.time = time;
            this.ok = ok;
        }

        public boolean isOk() {
            return ok;
        }

        public long getTime() {
            return time;
        }
    }
}
