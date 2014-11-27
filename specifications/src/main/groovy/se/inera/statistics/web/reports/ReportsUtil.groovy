package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.inera.statistics.spec.Intyg

class ReportsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsUtil.class);
    final VARDGIVARE = "vg-verksamhet1"

    def statistik = new RESTClient('http://localhost:8080/', ContentType.JSON)

    long getCurrentDateTime() {
        return get('/api/testsupport/now')
    }

    def setCurrentDateTime(long timeMillis) {
        def response = statistik.post(path: '/api/testsupport/now', body: String.valueOf(timeMillis))
        assert response.status == 200
    }

    def setCutoff(long cutoff) {
        def response = statistik.post(path: '/api/testsupport/cutoff', body: String.valueOf(cutoff))
        assert response.status == 200
    }

    def clearDatabase() {
        def response = statistik.post(path: '/api/testsupport/clearDatabase')
        assert response.status == 200
    }

    def insertIntyg(Intyg intyg) {
        def builder = new JsonBuilder(intyg)
        def response = statistik.put(path: '/api/testsupport/intyg', body: builder.toString())
        assert response.status == 200
    }

    def processIntyg() {
        def response = statistik.post(path: '/api/testsupport/processIntyg')
        assert response.status == 200
    }

    def getReportAntalIntyg() {
        return get("/api/getNumberOfCasesPerMonth")
    }

    def getReportAntalIntygInloggad() {
        return get("/api/verksamhet/" + VARDGIVARE + "/getNumberOfCasesPerMonth")
    }

    def getReportEnskiltDiagnoskapitel(String kapitel) {
        return get("/api/getDiagnosavsnittstatistik/" + kapitel)
    }

    private def get(String url) {
        def response = statistik.get(path: url)
        assert response.status == 200
        return response.data;
    }

    def getReportEnskiltDiagnoskapitelInloggad(String kapitel) {
        return get("/api/verksamhet/" + VARDGIVARE + "/getDiagnosavsnittstatistik/" + kapitel)
    }

    def getReportDiagnosgruppInloggad() {
        return get("/api/verksamhet/" + VARDGIVARE + "/getDiagnoskapitelstatistik")
    }

    def getReportDiagnosgrupp() {
        return get("/api/getDiagnoskapitelstatistik")
    }

    def login(String enhet) {
        def loginData = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"HSA-BS\"," +
                "\"enhetId\":\"" + enhet + "\"," +
                "\"vardgivarId\":\"" + VARDGIVARE + "\"," +
                "\"vardgivarniva\":\"false\"" +
                "}"
        def response = statistik.post(path: '/fake', body: [ userJsonDisplay:loginData ], requestContentType : "application/x-www-form-urlencoded" )
//        assert response.status == 200
    }

}
