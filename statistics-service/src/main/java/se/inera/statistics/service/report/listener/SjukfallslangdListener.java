package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
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
        accept(token, endMonth, firstDate, prevEnd, 12);
        accept(token, endMonth, firstDate, prevEnd, 3);
    }

    private void accept(GenericHolder token, LocalDate endMonth, LocalDate firstDate, LocalDate prevEnd, int periods) {
        if (prevEnd != null) {
            firstDate = prevEnd.plusMonths(periods);
        }
        LocalDate lastMonth = endMonth.plusMonths(periods - 1);
        for (LocalDate currentMonth = firstDate; !currentMonth.isAfter(lastMonth); currentMonth = currentMonth.plusMonths(1)) {
            String period = ReportUtil.toPeriod(currentMonth);
            accept(token, period, periods);
        }
    }
    
    void accept(GenericHolder token, String period, int periods) {
        String group = SjukfallslangdUtil.lookupGroupForLangd(token.getSjukfallInfo().getLangd());
        langdGrupp.count(period, token.getEnhetId(), group, periods, Verksamhet.ENHET, token.getKon());
        langdGrupp.count(period, token.getVardgivareId(), group, periods, Verksamhet.VARDGIVARE, token.getKon());
    }

    @Override
    void accept(GenericHolder token, String period) {
        throw new NoSuchMethodError("Not implemented");
    }

}
