package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

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
