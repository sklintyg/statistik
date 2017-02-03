package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class ProcessMeddelanden {

    private ReportsUtil reportsUtil = new ReportsUtil()

    ProcessMeddelanden(int times) {

        for (int i = 0; i < times; i++) {
            reportsUtil.processMeddelande()
        }
    }
}
