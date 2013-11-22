package se.inera.statistics.service.report.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.processlog.ProcessorListener;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DistributingListener implements ProcessorListener {
    private static final Logger LOG = LoggerFactory.getLogger(DistributingListener.class);

    @Autowired
    private SjukfallPerKonListener sjukfallPerKonListener;

    @Autowired
    private SjukfallPerDiagnosgruppListener sjukfallPerDiagnosgruppListenerListener;

    @Autowired
    private SjukfallPerDiagnosundergruppListener sjukfallPerDiagnosundergruppListenerListener;

    @Autowired
    private AldersGruppListener aldersgruppListener;

    @Autowired
    private SjukfallslangdListener sjukfallsLangdListener;

    @Autowired
    private SjukskrivningsgradListener sjukskrivningsgradListener;

    @Autowired
    private SjukfallPerLanListener sjukfallPerLanListener;

    @Autowired
    private ProcessLog processLog;
    private long latestLogId;
    private final Object lock = new Object();

    @Override
    @Transactional
    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, long logId) {
        boolean cacheFull = aldersgruppListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerKonListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerDiagnosgruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerDiagnosundergruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallsLangdListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukskrivningsgradListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerLanListener.accept(sjukfallInfo, utlatande, hsa);

        synchronized (lock) {
            latestLogId = logId;
            if (cacheFull) {
                persistCaches();
            }
        }
    }

    public void persistCaches() {
        synchronized (lock) {
            aldersgruppListener.persistCache();
            sjukfallPerDiagnosgruppListenerListener.persistCache();
            processLog.confirm(latestLogId);
        }
        LOG.info("Data counters persisted until about logId: " + latestLogId);
    }

}
