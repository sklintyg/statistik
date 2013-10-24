package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallslangdListener extends GenericAbstractListener {

    @Autowired
    private SjukfallslangdGrupp langdGrupp;

    @Override
    void accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth) {
        LocalDate firstDate = firstMonth;
        LocalDate prevEnd = token.getSjukfallInfo().getPrevEnd();
        for (RollingLength length: RollingLength.values()) {
            accept(token, firstDate, endMonth, prevEnd, length);
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

    void accept(GenericHolder token, String period, RollingLength length) {
        String group = SjukfallslangdUtil.RANGES.rangeFor(token.getSjukfallInfo().getLangd()).getName();
        langdGrupp.count(period, token.getEnhetId(), group, length, Verksamhet.ENHET, token.getKon());
        langdGrupp.count(period, token.getVardgivareId(), group, length, Verksamhet.VARDGIVARE, token.getKon());
    }

    @Override
    void accept(GenericHolder token, String period) {
        throw new NoSuchMethodError("Not implemented");
    }

}
