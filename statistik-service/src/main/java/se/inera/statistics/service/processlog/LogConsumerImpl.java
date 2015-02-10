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
package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSADecorator;

import java.util.List;

@Component
public class LogConsumerImpl implements LogConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(LogConsumerImpl.class);
    public static final int BATCH_SIZE = 100;

    @Autowired
    private ProcessLog processLog;

    @Autowired
    private Processor processor;

    @Autowired
    private HSADecorator hsa;

    private volatile boolean isRunning = false;

    @Transactional(noRollbackFor = Exception.class)
    public synchronized int processBatch() {
        try {
            setRunning(true);
            List<IntygEvent> result = processLog.getPending(BATCH_SIZE);
            int processed = 0;
            if (result.isEmpty()) {
                return 0;
            }
            for (IntygEvent event: result) {
                EventType type = event.getType();
                JsonNode intyg = JSONParser.parse(event.getData());
                JsonNode hsaInfo = hsa.decorate(intyg, event.getCorrelationId());
                if (hsaInfo != null || type.equals(EventType.REVOKED)) {
                    try {
                        processor.accept(intyg, hsaInfo, event.getId(), event.getCorrelationId(), type);
                    } catch (Exception e) {
                        LOG.error("Could not process intyg {} ({}). {}", event.getId(), event.getCorrelationId(), e.getMessage());
                    }
                    processLog.confirm(event.getId());
                    processed++;
                    LOG.info("Processed log id {}", event.getId());
                } else {
                    return processed;
                }
            }
            return processed;
        } finally {
            setRunning(false);
        }
    }
    public synchronized boolean isRunning() {
        return isRunning;
    }
    private synchronized void setRunning(boolean b) {
        isRunning = b;
    }
}
