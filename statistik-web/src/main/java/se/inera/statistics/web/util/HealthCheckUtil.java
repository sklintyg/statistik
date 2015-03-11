/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.web.service.ChartDataService;

public class HealthCheckUtil {

    private static final int NANOS_PER_MS = 1_000_000;

    @Value("${highcharts.export.url}")
    private String highchartsUrl;

    @Autowired
    private ChartDataService chartDataService;

    @Autowired
    private HSAWebServiceCalls hsaService;

    private HttpClient client;

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
        return createStatus(ok, startTime, doneTime);
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
        return createStatus(ok, startTime, doneTime);
    }

    public Status getHighchartsExportStatus() {
        boolean ok;

        if (client == null) {
            client = new HttpClient();
        }
        long startTime = System.nanoTime();
        try {
            ok = client.executeMethod(new GetMethod(highchartsUrl)) == HttpStatus.METHOD_NOT_ALLOWED.value();
        } catch (IOException e) {
            // Squelch this as it is quite ok to throw IOException.
            // It simply means that the service is not reachable
            ok = false;
        }
        long doneTime = System.nanoTime();
        return createStatus(ok, startTime, doneTime);
    }

    public Status getWorkloadStatus() {
        return new Status(CalcCoordinator.getWorkloadPercentage(), true);
    }

    private Status createStatus(boolean ok, long startTime, long doneTime) {
        return new Status((doneTime - startTime) / NANOS_PER_MS, ok);
    }

    public static final class Status {
        private final long measurement;
        private final boolean ok;

        private Status(long measurement, boolean ok) {
            this.measurement = measurement;
            this.ok = ok;
        }

        public boolean isOk() {
            return ok;
        }

        public long getMeasurement() {
            return measurement;
        }
    }
}
