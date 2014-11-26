package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class Troskelvarde {

    private ReportsUtil reportsUtil = new ReportsUtil()

    Troskelvarde(int cutoff) {
        reportsUtil.setCutoff(cutoff);
    }

}
