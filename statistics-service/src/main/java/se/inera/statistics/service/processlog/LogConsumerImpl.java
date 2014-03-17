package se.inera.statistics.service.processlog;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSADecorator;

import com.fasterxml.jackson.databind.JsonNode;

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

    @Transactional
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
