package se.inera.statistics.service.report.listener;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallslangdListener extends RollingAbstractListener {

    @Autowired
    private SjukfallslangdGrupp langdGrupp;

    protected boolean accept(GenericHolder token, String period, RollingLength length) {
        String group = SjukfallslangdUtil.RANGES.rangeFor(token.getSjukfallInfo().getLangd()).getName();
        langdGrupp.count(period, token.getEnhetId(), group, length, Verksamhet.ENHET, token.getKon());
        langdGrupp.count(period, token.getVardgivareId(), group, length, Verksamhet.VARDGIVARE, token.getKon());
        return false; // TODO: Caching
    }

    @Override
    protected void regroup(GenericHolder token, LocalDate currentMonth, String period, RollingLength length) {
        int days = Days.daysBetween(token.getSjukfallInfo().getStart(), token.getSjukfallInfo().getPrevEnd()).getDays() + 1;
        String group = SjukfallslangdUtil.RANGES.rangeFor(days).getName();
        String newGroup = SjukfallslangdUtil.RANGES.rangeFor(token.getSjukfallInfo().getLangd()).getName();
        if (!newGroup.equals(group)) {
            langdGrupp.recount(period, token.getEnhetId(), group, newGroup, length, Verksamhet.ENHET, token.getKon());
            langdGrupp.recount(period, token.getVardgivareId(), group, newGroup, length, Verksamhet.VARDGIVARE, token.getKon());
        }
    }

}
