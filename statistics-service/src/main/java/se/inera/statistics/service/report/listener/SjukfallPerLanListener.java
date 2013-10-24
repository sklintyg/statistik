package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerCounty;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallPerLanListener extends RollingAbstractListener {

    @Autowired
    private CasesPerCounty casesPerCounty;

    protected void accept(GenericHolder token, String period, RollingLength length) {
        casesPerCounty.count(period, token.getEnhetId(), token.getLanId(), length, Verksamhet.ENHET, token.getKon());
    }

}
