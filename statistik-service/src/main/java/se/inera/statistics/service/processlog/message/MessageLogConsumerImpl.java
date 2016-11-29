/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.ifv.statistics.spi.authorization.impl.HsaCommunicationException;
import se.inera.statistics.service.helper.SendMessageToCareHelper;
import se.inera.statistics.service.processlog.Processor;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

import java.util.List;

@Component
public class MessageLogConsumerImpl implements MessageLogConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(MessageLogConsumerImpl.class);
    public static final int BATCH_SIZE = 100;

    @Autowired
    private ProcessMessageLog processLog;

    @Autowired
    private Processor processor;

    @Autowired
    private SendMessageToCareHelper sendMessageToCareHelper;

    private volatile boolean isRunning = false;

    @Transactional
    @Override
    public synchronized int processBatch() {
        try {
            setRunning(true);
            List<MessageEvent> result = processLog.getPending(BATCH_SIZE);
            if (result.isEmpty()) {
                return 0;
            }
            int processed = 0;
            for (MessageEvent event: result) {
                try {
                    final boolean eventSuccessfullyHandled = handleEvent(event);
                    if (!eventSuccessfullyHandled) {
                        LOG.error("Failed to process meddelande {} ({})", event.getId(), event.getCorrelationId());
                    }
                } catch (HsaCommunicationException e) {
                    LOG.error("Could not process meddelande {} ({}). {}", event.getId(), event.getCorrelationId(), e.getMessage());
                    LOG.debug("Could not process meddelande {} ({}).", event.getId(), event.getCorrelationId(), e);
                    return processed;
                } catch (Exception e) {
                    LOG.error("Could not process meddelande {} ({}). {}", event.getId(), event.getCorrelationId(), e.getMessage());
                    LOG.debug("Could not process meddelande {} ({}).", event.getId(), event.getCorrelationId(), e);
                } finally {
                    processLog.confirm(event.getId());
                    processed++;
                    LOG.info("Processed message log id {}", event.getId());
                }
            }
            return processed;
        } finally {
            setRunning(false);
        }
    }

    private boolean handleEvent(MessageEvent event) {
        try {
            final SendMessageToCareType rc = sendMessageToCareHelper.unmarshalSendMessageToCareTypeXml(event.getData());

            processor.accept(rc, event.getId(), event.getCorrelationId(), event.getType());
        } catch (Exception e) {
            LOG.warn("Failed to unmarshal meddelande xml");
            LOG.debug("Failed to unmarshal meddelande xml", e);
            return false;
        }
        return true;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    private synchronized void setRunning(boolean b) {
        isRunning = b;
    }

}
