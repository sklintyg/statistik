package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.api.SjukfallPerLan;
import se.inera.statistics.service.report.repository.RollingLength;

@Component
public class SjukfallPerLanListener extends RollingAbstractListener {

    @Autowired
    private SjukfallPerLan sjukfallPerLan;

    protected boolean accept(GenericHolder token, String period, RollingLength length) {
        sjukfallPerLan.count(period, token.getEnhetId(), token.getLanId(), length, token.getKon());
        return false; // TODO: Caching
    }

}
