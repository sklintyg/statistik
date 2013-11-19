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

    @Scheduled(cron = "${scheduler.logJob.cron}")
//    @Scheduled(fixedDelayString = "500")
    public void checkLog() {
        LOG.debug("Log Job");
        int count;
        do {
            count = consumer.processBatch();
            LOG.info("Processed batch with {} entries", count);
        } while (count > 0);
    }
}
