/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.util;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.service.scheduler.ReceiveHistoryJob;
import se.inera.statistics.web.service.ChartDataService;

public class HealthCheckUtil {

    private static final int NANOS_PER_MS = 1_000_000;

    @Autowired
    private ChartDataService chartDataService;

    @Autowired
    private HSAWebServiceCalls hsaService;

    @Autowired
    private ReceiveHistoryJob receiveHistory;

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

    public Status getReceiveStatus() {
        return new Status(receiveHistory.getCurrentRate(), true);
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
