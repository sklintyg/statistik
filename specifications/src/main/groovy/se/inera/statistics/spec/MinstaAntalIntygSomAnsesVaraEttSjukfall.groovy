package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class MinstaAntalIntygSomAnsesVaraEttSjukfall {

    private ReportsUtil reportsUtil = new ReportsUtil()

    MinstaAntalIntygSomAnsesVaraEttSjukfall(int cutoff) {
        reportsUtil.setCutoff(cutoff);
    }

}
