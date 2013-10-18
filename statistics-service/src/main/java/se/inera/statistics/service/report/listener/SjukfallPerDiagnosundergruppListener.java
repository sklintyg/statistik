package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.DiagnosisSubGroups;

@Component
public class SjukfallPerDiagnosundergruppListener extends GenericAbstractListener {

    @Autowired
    private DiagnosisSubGroups diagnosisgroupPersistenceHandler;

    @Override
    void accept(GenericHolder token, String period) {
        diagnosisgroupPersistenceHandler.count(token.getEnhetId(), period, token.getDiagnosgrupp(), token.getDiagnosundergrupp(), token.getKon());
        diagnosisgroupPersistenceHandler.count(token.getVardgivareId(), period, token.getDiagnosgrupp(), token.getDiagnosundergrupp(), token.getKon());
    }
}
