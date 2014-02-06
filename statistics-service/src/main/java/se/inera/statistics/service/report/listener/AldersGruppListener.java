package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.Aldersgrupp;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class AldersGruppListener extends RollingAbstractListener {

    @Autowired
    private Aldersgrupp aldersgrupp;

    protected boolean accept(GenericHolder token, String period, RollingLength length) {
        String group = AldersgroupUtil.RANGES.rangeFor(token.getAge()).getName();
        aldersgrupp.count(period, token.getEnhetId(), group, length,
        Verksamhet.ENHET, token.getKon());
        aldersgrupp.count(period, token.getVardgivareId(), group, length,
        Verksamhet.VARDGIVARE, token.getKon());
        return true;
    }

}
