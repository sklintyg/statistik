package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.api.FallPerLan;
import se.inera.statistics.service.report.repository.RollingLength;

@Component
public class SjukfallPerLanListener extends RollingAbstractListener {

    @Autowired
    private FallPerLan fallPerLan;

    protected boolean accept(GenericHolder token, String period, RollingLength length) {
        fallPerLan.count(period, token.getEnhetId(), token.getLanId(), length, token.getKon());
        return false; // TODO: Caching
    }

}
