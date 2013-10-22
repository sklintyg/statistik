package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa) {
        sjukfallPerKonListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallPerDiagnosgruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallPerDiagnosundergruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        aldersgruppListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallsLangdListener.accept(sjukfallInfo, utlatande, hsa);
    }

}
