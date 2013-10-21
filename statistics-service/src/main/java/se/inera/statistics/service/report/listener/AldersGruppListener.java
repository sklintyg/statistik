package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class AldersGruppListener extends GenericAbstractListener {

    public static final int PERIODS = 12;

    @Autowired
    private AgeGroups ageGroups;

    @Override
    void accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth) {
        LocalDate firstDate = firstMonth;
        LocalDate prevEnd = token.getSjukfallInfo().getPrevEnd();
        if (prevEnd != null) {
            firstDate = prevEnd.plusMonths(PERIODS);
        }
        LocalDate lastMonth = endMonth.plusMonths(PERIODS - 1);
        for (LocalDate currentMonth = firstDate; !currentMonth.isAfter(lastMonth); currentMonth = currentMonth.plusMonths(1)) {
            accept(token, currentMonth);
        }
    }

    @Override
    void accept(GenericHolder token, String period) {
        String group = AldersgroupUtil.lookupGroupForAge(token.getAge());
        ageGroups.count(period, token.getEnhetId(), group, Verksamhet.ENHET, token.getKon());
        ageGroups.count(period, token.getVardgivareId(), group, Verksamhet.VARDGIVARE, token.getKon());
    }
}
