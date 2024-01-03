/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.monitoring;

import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.web.api.ChartDataService;

/**
 * Exposes health metrics as Prometheus values. To simplify any 3rd party scraping applications, all metrics produced
 * by this component uses the following conventions:
 *
 * All metrics are prefixed with "health_"
 * All metrics are suffixed with their type, either "_normal" that indicates a boolean value 0 or 1 OR
 * "_value" that indiciates a numeric metric of some kind.
 *
 * Note that NORMAL values uses 0 to indicate OK state and 1 to indicate a problem.
 *
 * @author eriklupander
 */
@Component
public class HealthMonitor extends Collector {

    private static final String PREFIX = "health_";
    private static final String NORMAL = "_normal";
    private static final String VALUE = "_value";

    private static final long MILLIS_PER_SECOND = 1000L;

    private static final String PING_SQL = "SELECT 1";

    @Autowired
    private CalcCoordinator calcCoordinator;

    @Autowired
    private ChartDataService chartDataService;

    @PersistenceContext
    private EntityManager entityManager;

    private Gauge uptime, workloadStatus, dbAccessible, chartserviceAccessible;
    private AtomicBoolean initialized = new AtomicBoolean();
    private long startTime;

    @FunctionalInterface
    interface Tester {

        void run() throws Exception;
    }

    /**
     * Registers this class as a prometheus collector.
     */
    @PostConstruct
    public void init() {
        this.startTime = System.currentTimeMillis();
        this.uptime = register("uptime", "Current uptime in seconds", VALUE);
        this.workloadStatus = register("workload_status", "Current workload status", VALUE);
        this.dbAccessible = register("db_accessible", "0 == OK 1 == NOT OK", NORMAL);
        this.chartserviceAccessible = register("chartservice_accessible", "0 == OK 1 == NOT OK", NORMAL);
        this.register();
        this.initialized.set(true);
    }

    @Override
    public List<MetricFamilySamples> collect() {
        if (initialized.get()) {
            uptime.set(getUptime());
            dbAccessible.set(checkDbConnection() ? 0 : 1);
            chartserviceAccessible.set(getOverviewStatus() ? 0 : 1);
            workloadStatus.set(calcCoordinator.getWorkloadPercentage());
        }
        return Collections.emptyList();
    }

    private long getUptime() {
        return (System.currentTimeMillis() - startTime) / MILLIS_PER_SECOND;
    }

    private boolean checkDbConnection() {
        return invoke(() -> entityManager.createNativeQuery(PING_SQL).getSingleResult());
    }

    private Gauge register(String name, String help, String type) {
        return Gauge.build()
            .name(PREFIX + name + type).help(help)
            .register();
    }

    private boolean getOverviewStatus() {
        try {
            chartDataService.getOverviewData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean invoke(Tester tester) {
        try {
            tester.run();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
