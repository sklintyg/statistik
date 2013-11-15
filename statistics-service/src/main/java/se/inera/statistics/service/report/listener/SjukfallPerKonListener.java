package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallPerKonListener extends GenericAbstractListener {

    @Autowired
    private CasesPerMonth casesPerMonthPersistenceHandler;

    @Override
    boolean accept(GenericHolder token, String period) {
        casesPerMonthPersistenceHandler.count(token.getEnhetId(), period, Verksamhet.ENHET, token.getKon());
        casesPerMonthPersistenceHandler.count(token.getVardgivareId(), period, Verksamhet.VARDGIVARE, token.getKon());
        return false; // TODO: Caching
    }

}
