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
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

public class MessageJob {
    private static final Logger LOG = LoggerFactory.getLogger(MessageJob.class);
    private static final String JOB_NAME = "MessageJob.run";

    private MessageLogConsumer consumer;

    public MessageJob(MessageLogConsumer consumer) {
        this.consumer = consumer;
    }

    @Scheduled(cron = "${scheduler.logJob.cron}")
    @SchedulerLock(name = JOB_NAME)
    public void run() {
        LOG.info(JOB_NAME);
        long startId;
        long latestHandledId = 0;
        do {
            startId = latestHandledId;
            latestHandledId = consumer.processBatch(startId);
            LOG.info("Processed batch from id {} to {}", startId, latestHandledId);
        } while (startId != latestHandledId);
    }
}
