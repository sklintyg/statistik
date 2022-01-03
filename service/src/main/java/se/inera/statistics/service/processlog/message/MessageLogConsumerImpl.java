/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog.message;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.SendMessageToCareHelper;
import se.inera.statistics.service.processlog.Processor;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v2.SendMessageToCareType;

@Component
public class MessageLogConsumerImpl implements MessageLogConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageLogConsumerImpl.class);
    public static final int BATCH_SIZE = 100;

    @Value("#{'${process.messages.intervals:0,3,9,18,50,100,250,550,1500}'.split(',')}")
    private List<Integer> tryIntervals;

    @Autowired
    private ProcessMessageLog processLog;
    @Autowired
    private Processor processor;
    @Autowired
    private SendMessageToCareHelper sendMessageToCareHelper;

    private volatile boolean isRunning = false;

    @Transactional(noRollbackFor = Exception.class)
    @Override
    public synchronized long processBatch(long startId) {
        try {
            setRunning(true);
            int maxNumberOfTries = tryIntervals.get(tryIntervals.size() - 1);

            List<MessageEvent> result = processLog.getPending(BATCH_SIZE, startId, maxNumberOfTries);
            if (result.isEmpty()) {
                return startId;
            }
            int processed = 0;
            long latestId = startId;

            for (MessageEvent event : result) {
                if (!tryIntervals.contains(event.getTries())) {
                    increaseTries(event);
                } else {
                    final boolean eventSuccessfullyHandled = handleEvent(event);
                    if (!eventSuccessfullyHandled) {
                        LOG.error("Failed to process meddelande {} ({})", event.getId(), event.getCorrelationId());
                        increaseTries(event);
                    }

                    LOG.info("Processed message log id {}", event.getId());
                }
                processed++;
                latestId = event.getId();
            }
            LOG.info("Processed message batch with {} entries", processed);

            return latestId;
        } finally {
            setRunning(false);
        }
    }

    private void increaseTries(MessageEvent event) {
        processLog.increaseNumberOfTries(event.getId());
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
