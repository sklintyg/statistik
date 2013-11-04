package se.inera.statistics.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.processlog.LogConsumer;

@Component
public class LogJob {
    private static final Logger LOG = LoggerFactory.getLogger(LogJob.class);

    @Autowired
    private LogConsumer consumer;

    @Scheduled(cron = "0/10 * * * * ?")
    public void checkLog() {
        LOG.debug("Log Job");
        while (consumer.processBatch() > 0) {
            System.err.println("Processed batch");
        }
    }
}
