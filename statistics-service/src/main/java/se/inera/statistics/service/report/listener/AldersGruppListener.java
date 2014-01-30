package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.db.AldersgruppKey;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Verksamhet;

import java.util.HashMap;

@Component
public class AldersGruppListener extends RollingAbstractListener {

    @Autowired
    private AgeGroups ageGroups;

    protected boolean accept(GenericHolder token, String period, RollingLength length) {
        String group = AldersgroupUtil.RANGES.rangeFor(token.getAge()).getName();
        ageGroups.count(period, token.getEnhetId(), group, length,
        Verksamhet.ENHET, token.getKon());
        ageGroups.count(period, token.getVardgivareId(), group, length,
        Verksamhet.VARDGIVARE, token.getKon());
        return false;
    }

}
