package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class BlockeraBerakning {

    private ReportsUtil reportsUtil = new ReportsUtil()

    BlockeraBerakning(String status) {
        if ("true".equals(status)) {
            reportsUtil.denyCalc()
        } else {
            reportsUtil.allowCalc()
        }
    }

}
