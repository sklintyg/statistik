package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class TaBortExisterandeIntyg {

    private ReportsUtil reportsUtil = new ReportsUtil()

    TaBortExisterandeIntyg() {
        reportsUtil.clearDatabase()
    }

}
