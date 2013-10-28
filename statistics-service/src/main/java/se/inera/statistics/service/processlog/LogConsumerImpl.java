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
            List<IntygEvent> result = processLog.getPending(100);
            if (result.isEmpty()) {
                return 0;
            }
            for (IntygEvent event: result) {
                JsonNode intyg = JSONParser.parse(event.getData());
                JsonNode hsaInfo = hsa.syncDecorate(intyg, event.getCorrelationId());
                if (hsaInfo != null) {
                    processor.accept(intyg, hsaInfo);
                    processLog.confirm(event.getId());
                } else {
                    return 0;
                }
            }
            return result.size();
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
