package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class AldersGruppListener extends GenericAbstractListener {

    @Autowired
    private AgeGroups ageGroups;

    @Override
    void accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth) {
        LocalDate prevEnd = token.getSjukfallInfo().getPrevEnd();
        for (RollingLength length: RollingLength.values()) {
            accept(token, firstMonth, endMonth, prevEnd, length);
        }
    }

    private void accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth, LocalDate prevEnd, RollingLength length) {
        LocalDate startMonth = prevEnd == null ? firstMonth : prevEnd.plusMonths(length.getPeriods());
        LocalDate lastMonth = endMonth.plusMonths(length.getPeriods() - 1);
        for (LocalDate currentMonth = startMonth; !currentMonth.isAfter(lastMonth); currentMonth = currentMonth.plusMonths(1)) {
            String period = ReportUtil.toPeriod(currentMonth);
            accept(token, period, length);
        }
    }

    private void accept(GenericHolder token, String period, RollingLength length) {
        String group = AldersgroupUtil.RANGES.rangeFor(token.getAge()).getName();
        ageGroups.count(period, token.getEnhetId(), group, length, Verksamhet.ENHET, token.getKon());
        ageGroups.count(period, token.getVardgivareId(), group, length, Verksamhet.VARDGIVARE, token.getKon());
    }

    @Override
    void accept(GenericHolder token, String period) {
        throw new NoSuchMethodError("Not implemented");
    }
}
