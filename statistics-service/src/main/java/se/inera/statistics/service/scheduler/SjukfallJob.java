package se.inera.statistics.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.statistics.service.sjukfall.SjukfallService;

import java.util.Date;

public class SjukfallJob {
    private static final Logger LOG = LoggerFactory.getLogger(SjukfallJob.class);

    @Autowired
    protected SjukfallService sjukfallService;

    @Scheduled(cron = "* * 2 * * *")
    public void cleanupSjukfall() {
        LOG.info("Running nightly clean up of sjukfall ...");
        int result = sjukfallService.expire(new Date());
        LOG.info("... Done running nightly clean up of sjukfall. " + result + " lines removed.");
    }
}
