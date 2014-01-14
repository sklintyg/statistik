package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallPerDiagnosundergruppListener extends GenericAbstractListener {

    @Autowired
    private Diagnoskapitel diagnosisgroupPersistenceHandler;

    @Override
    boolean accept(GenericHolder token, String period) {
        diagnosisgroupPersistenceHandler.count(token.getEnhetId(), period, token.getDiagnosgrupp(), token.getDiagnosundergrupp(), Verksamhet.ENHET, token.getKon());
        diagnosisgroupPersistenceHandler.count(token.getVardgivareId(), period, token.getDiagnosgrupp(), token.getDiagnosundergrupp(), Verksamhet.VARDGIVARE, token.getKon());
        return false; // TODO: Caching
    }
}
