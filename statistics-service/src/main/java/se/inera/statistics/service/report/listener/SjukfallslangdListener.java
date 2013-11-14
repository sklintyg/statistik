package se.inera.statistics.service.report.listener;

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

}
