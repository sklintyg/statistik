/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Time;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.transaction.annotation.Transactional;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.web.service.ChartDataService;
import se.inera.statistics.web.service.monitoring.SessionCounterListener;

public class HealthCheckUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckUtil.class);
    private static final long START_TIME = System.currentTimeMillis();
    private static final int NANOS_PER_MS = 1_000_000;
    private static final String CURR_TIME_SQL = "SELECT CURRENT_TIME()";

    @Autowired
    private ChartDataService chartDataService;

    @Autowired
    private HSAWebServiceCalls hsaService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SessionRegistry sessionRegistry;

    public Status getOverviewStatus() {
        boolean ok;
        long startTime = System.nanoTime();
        try {
            chartDataService.getOverviewData();
            ok = true;
        } catch (Exception e) {
            LOG.debug("Could not get overview data", e);
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
            LOG.debug("Could not call hsa ping", e);
            ok = false;
        }
        long doneTime = System.nanoTime();
        return createStatus(ok, startTime, doneTime);
    }

    public Status getWorkloadStatus() {
        return new Status(CalcCoordinator.getWorkloadPercentage(), true);
    }

    public Status checkUptime() {
        long uptime = System.currentTimeMillis() - START_TIME;
        LOG.debug("Current system uptime is {}", DurationFormatUtils.formatDurationWords(uptime, true, true));
        return new Status(uptime, true);
    }

    public String getUptimeAsString() {
        Status uptime = checkUptime();
        return DurationFormatUtils.formatDurationWords(uptime.getMeasurement(), true, true);
    }

    @Transactional
    public Status checkDB() {
        boolean ok;
        long startTime = System.nanoTime();
        ok = checkTimeFromDb();
        long doneTime = System.nanoTime();
        return createStatus(ok, startTime, doneTime);
    }

    private boolean checkTimeFromDb() {
        Time timestamp;
        try {
            Query query = entityManager.createNativeQuery(CURR_TIME_SQL);
            timestamp = (Time) query.getSingleResult();
        } catch (Exception e) {
            LOG.error("checkTimeFromDb failed with exception: " + e.getMessage());
            return false;
        }
        return timestamp != null;
    }

    public Status checkNbrOfLoggedInUsers() {
        boolean ok;
        long size = -1;
        try {
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            size = allPrincipals.size();
            ok = true;
        } catch (Exception e) {
            LOG.warn("Operation checkNbrOfLoggedInUsers failed", e);
            ok = false;
        }

        String result = ok ? "OK" : "FAIL";
        LOG.debug("Operation checkNbrOfLoggedInUsers completed with result {}, nbr of users is {}", result, size);

        return new Status(size, ok);
    }

    public Status getNumberOfUsers() {
        long size = SessionCounterListener.getTotalActiveSession();

        return new Status(size, true);
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
