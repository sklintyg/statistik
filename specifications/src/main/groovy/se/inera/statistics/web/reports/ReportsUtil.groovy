package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.inera.statistics.spec.Intyg

class ReportsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsUtil.class);
    public static final VARDGIVARE = "vg1"
    public static final VARDGIVARE3 = "vg3"

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

    def getReportAntalIntygInloggad(String user) {
        return get("/api/verksamhet/" + getVardgivareForUser(user) + "/getNumberOfCasesPerMonth")
    }

    def getReportEnskiltDiagnoskapitel(String kapitel) {
        return get("/api/getDiagnosavsnittstatistik/" + kapitel)
    }

    private def get(String url) {
        def response = statistik.get(path: url)
        assert response.status == 200
        return response.data;
    }

    def getReportEnskiltDiagnoskapitelInloggad(String kapitel, String user) {
        return get("/api/verksamhet/" + getVardgivareForUser(user) + "/getDiagnosavsnittstatistik/" + kapitel)
    }

    def getReportDiagnosgruppInloggad(String user) {
        return get("/api/verksamhet/" + getVardgivareForUser(user) + "/getDiagnoskapitelstatistik")
    }

    def getReportDiagnosgrupp() {
        return get("/api/getDiagnoskapitelstatistik")
    }

    def getVardgivareForUser(String user) {
        switch (user) {
            case "user1": return VARDGIVARE;
            case "user2": return VARDGIVARE;
            case "user3": return VARDGIVARE3;
            default: throw new RuntimeException("Unknown user: " + user)
        }
    }

    def getVardgivareForEnhet(String enhet, String defaultVardgivare) {
        switch (enhet) {
            case "enhet1": return VARDGIVARE;
            case "enhet2": return VARDGIVARE;
            case "enhet3": return VARDGIVARE3;
            default: return defaultVardgivare;
        }
    }


    def login(String user, boolean vardgivarniva) {
        def logins = [:];
        logins["user1"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user1\"," +
                "\"enhetId\":\"enhet1\"," +
                "\"vardgivarId\":\"" + getVardgivareForUser(user) + "\"," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user2"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user2\"," +
                "\"enhetId\":\"enhet1\"," +
                "\"vardgivarId\":\"" + getVardgivareForUser(user) + "\"," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user3"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user3\"," +
                "\"enhetId\":\"enhet3\"," +
                "\"vardgivarId\":\"" + getVardgivareForUser(user) + "\"," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        def loginData = logins[user]
        def response = statistik.post(path: '/fake', body: [ userJsonDisplay:loginData ], requestContentType : "application/x-www-form-urlencoded" )
//        assert response.status == 200
    }

}
