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

    public LogConsumerImpl() {
    }

    @Transactional(noRollbackFor = Exception.class)
    public int processBatch() {
        try {
            setRunning(true);
            List<IntygEvent> result = processLog.getPending(BATCH_SIZE);
            int processed = 0;
            if (result.isEmpty()) {
                return 0;
            }
            for (IntygEvent event: result) {
                if (event.getType() == EventType.REVOKED) {
                    LOG.info("Event was delete event, skipping: " + event.getId());
                    processed++;
                    continue;
                }
                JsonNode intyg = JSONParser.parse(event.getData());
                JsonNode hsaInfo = hsa.decorate(intyg, event.getCorrelationId());
                if (hsaInfo != null) {
                    try {
                        processor.accept(intyg, hsaInfo, event.getId());
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
