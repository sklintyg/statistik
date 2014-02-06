package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallPerKonListener extends GenericAbstractListener {

    @Autowired
    private SjukfallPerManad sjukfallPerManadPersistenceHandler;

    @Override
    boolean accept(GenericHolder token, String period) {
        sjukfallPerManadPersistenceHandler.count(token.getEnhetId(), period, Verksamhet.ENHET, token.getKon());
        sjukfallPerManadPersistenceHandler.count(token.getVardgivareId(), period, Verksamhet.VARDGIVARE, token.getKon());
        return false;
    }

}
