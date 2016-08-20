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

    public void reset() {
        användare = null
        filnamn = null
        statusmeddelande = null
    }

    public void setKommentar(String kommentar) {}

    def statusmeddelande() {
        return statusmeddelande
    }

    public void execute() {
        def vgId = reportsUtil.getVardgivareForUser(användare)
        reportsUtil.insertLandsting(vgId)

        reportsUtil.login(användare, true)
        def file = getClass().getResourceAsStream('/' + filnamn)
        if (file == null) {
            throw new RuntimeException("File not found: " + filnamn)
        }
        def result = reportsUtil.uploadFile(vgId, file, filnamn)
        statusmeddelande = result.message
    }

}
