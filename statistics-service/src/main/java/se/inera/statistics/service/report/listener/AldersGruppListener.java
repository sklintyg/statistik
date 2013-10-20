package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class AldersGruppListener extends GenericAbstractListener {

    @Autowired
    private AgeGroups ageGroups;

    @Override
    void accept(GenericHolder token, String period) {
        String group = AldersgroupUtil.lookupGroupForAge(token.getAge());
        ageGroups.count(period, token.getEnhetId(), group, Verksamhet.ENHET, token.getKon());
        ageGroups.count(period, token.getVardgivareId(), group, Verksamhet.VARDGIVARE, token.getKon());
    }
}
