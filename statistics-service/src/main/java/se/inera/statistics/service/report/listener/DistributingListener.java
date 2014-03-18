package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.processlog.ProcessorListener;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DistributingListener implements ProcessorListener {
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

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, long logId) {
        boolean cacheFull = aldersgruppListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerKonListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerDiagnosgruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerDiagnosundergruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallsLangdListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukskrivningsgradListener.accept(sjukfallInfo, utlatande, hsa);
        cacheFull = cacheFull | sjukfallPerLanListener.accept(sjukfallInfo, utlatande, hsa);
    }
}
