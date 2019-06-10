/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.web.service.endpoints.ChartDataService;

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

    private static final long START_TIME = System.currentTimeMillis();

    private static final Gauge UPTIME = Gauge.build()
            .name(PREFIX + "uptime" + VALUE)
            .help("Current uptime in seconds")
            .register();

    private static final Gauge WORKLOAD_STATUS = Gauge.build()
            .name(PREFIX + "workload_status" + VALUE)
            .help("Current workload status")
            .register();

    private static final Gauge DB_ACCESSIBLE = Gauge.build()
            .name(PREFIX + "db_accessible" + NORMAL)
            .help("0 == OK 1 == NOT OK")
            .register();

    private static final Gauge CHARTSERVICE_ACCESSIBLE = Gauge.build()
            .name(PREFIX + "chartservice_accessible" + NORMAL)
            .help("0 == OK 1 == NOT OK")
            .register();

    private static final Gauge HSA_ACCESSIBLE = Gauge.build()
            .name(PREFIX + "hsa_accessible" + NORMAL)
            .help("0 == OK 1 == NOT OK")
            .register();

    private static final long MILLIS_PER_SECOND = 1000L;

    private static final String PING_SQL = "SELECT 1";

    @Autowired
    private ChartDataService chartDataService;

    @Autowired
    private HSAWebServiceCalls hsaService;

    @PersistenceContext
    private EntityManager entityManager;

    @FunctionalInterface
    interface Tester {
        void run() throws Exception;
    }

    /**
     * Registers this class as a prometheus collector.
     */
    @PostConstruct
    public void init() {
        this.register();
    }

    @Override
    public List<MetricFamilySamples> collect() {
        UPTIME.set(getUptime());
        DB_ACCESSIBLE.set(checkDbConnection() ? 0 : 1);
        HSA_ACCESSIBLE.set(getHsaStatus() ? 0 : 1);
        CHARTSERVICE_ACCESSIBLE.set(getOverviewStatus() ? 0 : 1);
        WORKLOAD_STATUS.set(getWorkloadStatus());

        return Collections.emptyList();
    }

    private long getUptime() {
        return (System.currentTimeMillis() - START_TIME) / MILLIS_PER_SECOND;
    }

    private boolean checkDbConnection() {
        return invoke(() -> entityManager.createNativeQuery(PING_SQL).getSingleResult());
    }

    private boolean getOverviewStatus() {
        try {
            chartDataService.getOverviewData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean getHsaStatus() {
        try {
            hsaService.callPing();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int getWorkloadStatus() {
        return CalcCoordinator.getWorkloadPercentage();
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
