package se.inera.statistics.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.listener.AldersGruppListener;

@Component
public class PersistListenerCacheJob {
    private static final Logger LOG = LoggerFactory.getLogger(PersistListenerCacheJob.class);

    @Autowired
    private AldersGruppListener aldersGruppListener;

    //@Scheduled(fixedDelayString = "${statistics.persist.listener.cache}")
    @Scheduled(cron = "0/10 * * * * ?")
    public void persistCache() {
        LOG.info("Persisting listener caches ...");
        aldersGruppListener.persistCache();
        LOG.info("... Done persisting listener caches.");
    }
}
