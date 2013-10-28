package se.inera.statistics.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.repository.NationellUpdater;

@Component
public class NationellUpdaterJob {
    private static final Logger LOG = LoggerFactory.getLogger(NationellUpdaterJob.class);

    @Autowired
    private NationellUpdater updater;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void checkLog() {
        LOG.info("Nationell Job");
        updater.updateAldersgrupp();
        updater.updateCasesPerMonth();
        updater.updateDiagnosgrupp();
        updater.updateDiagnosundergrupp();
        updater.updateSjukfallslangd();
        updater.updateSjukskrivningsgrad();
    }
}
