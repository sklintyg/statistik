package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class DagensDatum {

    private ReportsUtil reportsUtil = new ReportsUtil()

    DagensDatum(String dateString) {
        def date = Date.parse("yyyy-MM-dd", dateString)
        reportsUtil.setCurrentDateTime(date.time);
    }

}
