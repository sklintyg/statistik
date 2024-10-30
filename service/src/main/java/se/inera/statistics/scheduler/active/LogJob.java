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
package se.inera.statistics.scheduler.active;

import static se.inera.intyg.statistik.logging.MdcLogConstants.SPAN_ID_KEY;
import static se.inera.intyg.statistik.logging.MdcLogConstants.TRACE_ID_KEY;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.statistik.logging.MdcCloseableMap;
import se.inera.intyg.statistik.logging.MdcHelper;
import se.inera.intyg.statistik.logging.MdcLogConstants;
import se.inera.intyg.statistik.logging.PerformanceLogging;
import se.inera.statistics.service.monitoring.MonitoringLogService;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.processlog.intygsent.IntygsentLogConsumer;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

public class LogJob {

    private static final Logger LOG = LoggerFactory.getLogger(LogJob.class);
    private static final String JOB_NAME = "LogJob.run";

    private MonitoringLogService monitoringLogService;

    private LogConsumer consumer;
    private MessageLogConsumer messageLogConsumer;
    private IntygsentLogConsumer intygsentLogConsumer;
    private MdcHelper mdcHelper;

    public LogJob(MonitoringLogService monitoringLogService, LogConsumer consumer, IntygsentLogConsumer intygsentLogConsumer,
        MessageLogConsumer messageLogConsumer, MdcHelper mdcHelper) {
        this.monitoringLogService = monitoringLogService;
        this.consumer = consumer;
        this.intygsentLogConsumer = intygsentLogConsumer;
        this.messageLogConsumer = messageLogConsumer;
        this.mdcHelper = mdcHelper;
    }

    @Scheduled(cron = "${scheduler.logJob.cron}")
    @SchedulerLock(name = JOB_NAME)
    @PrometheusTimeMethod(help = "Jobb för att hantera inkomna intyg och meddelanden från kön")
    @PerformanceLogging(eventAction = "process-sent-certificates-and-messages-batch-job", eventType = MdcLogConstants.EVENT_TYPE_INFO)
    public void run() {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(TRACE_ID_KEY, mdcHelper.traceId())
                .put(SPAN_ID_KEY, mdcHelper.spanId())
                .build()
        ) {
            process();
        }
    }

    public void process() {
        LOG.info(JOB_NAME);
        int count;
        do {
            count = consumer.processBatch();
            LOG.info("Processed batch with {} entries", count);
            if (count > 0) {
                monitoringLogService.logInFromTable(count);
            }
        } while (count > 0);

        do {
            count = intygsentLogConsumer.processBatch();
            LOG.info("Processed sent batch with {} entries", count);
        } while (count > 0);

        // Process messages after intyg
        long startId;
        long latestHandledId = 0;
        do {
            startId = latestHandledId;
            latestHandledId = messageLogConsumer.processBatch(startId);
            LOG.info("Processed message batch from id {} to {}", startId, latestHandledId);
        } while (startId != latestHandledId);
    }
}