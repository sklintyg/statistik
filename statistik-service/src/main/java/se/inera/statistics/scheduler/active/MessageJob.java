/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.scheduler.active;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

@Component
public class MessageJob {
    private static final Logger LOG = LoggerFactory.getLogger(MessageJob.class);

    private final MessageLogConsumer consumer;

    @Autowired
    public MessageJob(MessageLogConsumer consumer) {
        this.consumer = consumer;
    }

    @Scheduled(cron = "${scheduler.logJob.cron}")
    public void checkLog() {
        LOG.debug("Message Job");
        int count;
        do {
            count = consumer.processBatch();
            LOG.info("Processed message batch with {} entries", count);
        } while (count > 0);
    }

}
