package se.inera.statistics.spec

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.joda.time.DateTimeUtils
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.testsupport.Intyg

class UppladdadeLandstingsfiler {

    protected final ReportsUtil reportsUtil = new ReportsUtil()

    def användare
    def filnamn
    def statusmeddelande

    public void setKommentar(String kommentar) {}

    def statusmeddelande() {
        return statusmeddelande
    }

    public void execute() {
        reportsUtil.login(användare, true)
        def file = getClass().getResourceAsStream('/' + filnamn)
        def result = reportsUtil.uploadFile(file, filnamn)
        statusmeddelande = result.message
    }

}
