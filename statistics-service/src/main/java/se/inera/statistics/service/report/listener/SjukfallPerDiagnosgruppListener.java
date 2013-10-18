package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallPerDiagnosgruppListener extends GenericAbstractListener {

    @Autowired
    private DiagnosisGroups diagnosisgroupPersistenceHandler;

    @Override
    void accept(GenericHolder token, String period) {
        diagnosisgroupPersistenceHandler.count(token.getEnhetId(), period, token.getDiagnosgrupp(), Verksamhet.ENHET, token.getKon());
        diagnosisgroupPersistenceHandler.count(token.getVardgivareId(), period, token.getDiagnosgrupp(), Verksamhet.VARDGIVARE, token.getKon());
    }
}
