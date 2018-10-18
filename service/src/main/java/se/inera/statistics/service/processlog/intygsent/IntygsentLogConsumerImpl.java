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
package se.inera.statistics.service.processlog.intygsent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.processlog.Processor;

@Component
public class IntygsentLogConsumerImpl implements IntygsentLogConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(IntygsentLogConsumerImpl.class);
    public static final int BATCH_SIZE = 100;

    @Autowired
    private ProcessIntygsentLog processLog;

    @Autowired
    private Processor processor;

    @Transactional
    @Override
    public synchronized int processBatch() {
        List<IntygSentEvent> result = processLog.getPending(BATCH_SIZE);
        if (result.isEmpty()) {
            return 0;
        }
        int processed = 0;
        for (IntygSentEvent event: result) {
            try {
                final String recipient = event.getRecipient();
                if (IntygSentHelper.isFkRecipient(recipient)) {
                    final String correlationId = event.getCorrelationId();
                    processor.acceptIntygSent(correlationId);
                } else {
                    LOG.info("Unknown intyg sent recipient: {}", recipient);
                }
                final long id = event.getId();
                processLog.confirm(id);
                LOG.info("Processed intyg sent id {}", id);
            } catch (Exception e) {
                LOG.error("Could not process 'intyg sent' event {} ({}). {}", event.getId(), event.getCorrelationId(), e.getMessage());
            } finally {
                processed++;
            }
        }
        return processed;
    }

}
