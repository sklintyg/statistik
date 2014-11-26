package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class TroskelVarde {

    private ReportsUtil reportsUtil = new ReportsUtil()

    TroskelVarde(int cutoff) {
        reportsUtil.setCutoff(cutoff);
    }

}
