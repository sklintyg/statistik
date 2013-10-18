package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.CasesPerMonth;

@Component
public class SjukfallPerKonListener extends GenericAbstractListener {

    @Autowired
    private CasesPerMonth casesPerMonthPersistenceHandler;

    @Override
    void accept(GenericHolder token, String period) {
        casesPerMonthPersistenceHandler.count(token.getEnhetId(), period, token.getKon());
        casesPerMonthPersistenceHandler.count(token.getVardgivareId(), period, token.getKon());
    }

}
