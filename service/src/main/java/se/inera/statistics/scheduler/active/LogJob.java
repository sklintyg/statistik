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
package se.inera.statistics.scheduler.active;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.statistics.service.monitoring.MonitoringLogService;
import se.inera.statistics.service.processlog.LogConsumer;

public class LogJob {
    private static final Logger LOG = LoggerFactory.getLogger(LogJob.class);
    private static final String JOB_NAME = "LogJob.checkLog";


    @Autowired
    private LogConsumer consumer;

    @Autowired
    @Qualifier("serviceMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Scheduled(cron = "${scheduler.logJob.cron}")
    @SchedulerLock(name = JOB_NAME)
    public void checkLog() {
        LOG.info(JOB_NAME);
        int count;
        do {
            count = consumer.processBatch();
            LOG.info("Processed batch with {} entries", count);
            if (count > 0) {
                monitoringLogService.logInFromTable(count);
            }
        } while (count > 0);
    }
}
