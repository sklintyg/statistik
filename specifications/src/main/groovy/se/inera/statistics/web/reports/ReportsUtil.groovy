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
        def response = statistik.get(path: '/api/testsupport/now')
        response.data;
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
        def response = statistik.get(path: "/api/getNumberOfCasesPerMonth")
        assert response.status == 200
        response.data;
    }

    def getReportAntalIntygInloggad() {
        def response = statistik.get(path: "api/verksamhet/" + VARDGIVARE + "/getNumberOfCasesPerMonth")
        assert response.status == 200
        response.data;
    }

    def getReportEnskiltDiagnoskapitel(String kapitel) {
        def response = statistik.get(path: "/api/getDiagnosavsnittstatistik/" + kapitel)
        assert response.status == 200
        response.data;
    }

    def getReportEnskiltDiagnoskapitelInloggad(String kapitel) {
        def response = statistik.get(path: "api/verksamhet/" + VARDGIVARE + "/getDiagnosavsnittstatistik/" + kapitel)
        assert response.status == 200
        response.data;
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
