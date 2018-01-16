package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class RensaUppladdadeLandstingsfiler {

    protected final ReportsUtil reportsUtil = new ReportsUtil()

    RensaUppladdadeLandstingsfiler() {
        reportsUtil.clearLandstingFileUploads()
    }

}
