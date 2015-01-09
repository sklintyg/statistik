package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.inera.statistics.web.service.ReportRequestFilter
import se.inera.testsupport.Intyg
import se.inera.testsupport.Personal

import static groovyx.net.http.ContentType.JSON

class ReportsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsUtil.class);
    public static final VARDGIVARE = "vg1"
    public static final VARDGIVARE3 = "vg3"

    def statistik = new RESTClient('http://localhost:8080/', JSON)

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

    def insertPersonal(Personal personal) {
        def builder = new JsonBuilder(personal)
        def response = statistik.put(path: '/api/testsupport/personal', body: builder.toString())
        assert response.status == 200
    }

    def processIntyg() {
        def response = statistik.post(path: '/api/testsupport/processIntyg')
        assert response.status == 200
    }

    def getReportAntalIntyg() {
        return get("/api/getNumberOfCasesPerMonth")
    }

    def getReportAntalIntygInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getNumberOfCasesPerMonth", filter)
    }

    def getReportLangaSjukfallInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getLongSickLeavesData", filter)
    }

    def getReportSjukfallPerEnhet(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getNumberOfCasesPerEnhet", filter)
    }

    def getReportEnskiltDiagnoskapitel(String kapitel) {
        return get("/api/getDiagnosavsnittstatistik/" + kapitel)
    }

    private def get(String url) {
        def response = statistik.get(path: url)
        assert response.status == 200
        return response.data;
    }

    private def post(String url, filter=new ReportRequestFilter(), queryString="") {
        def json = new JsonBuilder(filter)
        println("POSTING: " + json + " TO: " + url)
        def response = statistik.post(path: url, body: json.toString(), requestContentType: JSON, queryString : queryString)
        assert response.status == 200
        return response.data;
    }

    def getReportEnskiltDiagnoskapitelInloggad(String kapitel, String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getDiagnosavsnittstatistik/" + kapitel, filter)
    }

    def getReportDiagnosgruppInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getDiagnoskapitelstatistik", filter)
    }

    def getReportDiagnosgrupp() {
        return get("/api/getDiagnoskapitelstatistik")
    }

    def getVardgivareForUser(String user) {
        switch (user) {
            case "user1": return VARDGIVARE;
            case "user2": return VARDGIVARE;
            case "user3": return VARDGIVARE3;
            case "user4": return VARDGIVARE;
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
        logins["user4"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user4\"," +
                "\"enhetId\":\"enhet2\"," +
                "\"vardgivarId\":\"" + getVardgivareForUser(user) + "\"," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        def loginData = logins[user]
        def response = statistik.post(path: '/fake', body: [ userJsonDisplay:loginData ], requestContentType : "application/x-www-form-urlencoded" )
        assert response.status == 302
        System.out.println("Using logindata: " + loginData)
    }

    def getReportAldersgrupp() {
        return get("/api/getAgeGroupsStatistics")
    }

    def getReportAldersgruppInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getAgeGroupsStatistics", filter)
    }

    def getReportSjukskrivningslangd() {
        return get("/api/getSickLeaveLengthData")
    }

    def getReportSjukskrivningslangdInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getSickLeaveLengthData", filter)
    }

    def getReportAldersgruppPagaendeInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getAgeGroupsCurrentStatistics", filter)
    }

    def getReportSjukskrivningslangdPagaendeInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getSickLeaveLengthCurrentData", filter)
    }

    def getReportSjukskrivningsgradInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getDegreeOfSickLeaveStatistics", filter)
    }

    def getReportSjukskrivningsgrad() {
        return get("/api/getDegreeOfSickLeaveStatistics")
    }

    def getReportLakareAlderOchKonInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getCasesPerDoctorAgeAndGenderStatistics", filter)
    }

    def getReportLakarBefattningInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getNumberOfCasesPerLakarbefattning", filter)
    }

    def getReportSjukfallPerLakareInloggad(String user, filter) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getNumberOfCasesPerLakare", filter)
    }

    def getReportCasesPerSex() {
        return get("/api/getSjukfallPerSexStatistics")
    }

    def getReportCasesPerCounty() {
        return get("/api/getCountyStatistics")
    }

    def getReportJamforDiagnoserInloggad(String user, ReportRequestFilter filter, String diagnoserQueryString) {
        return post("/api/verksamhet/" + getVardgivareForUser(user) + "/getJamforDiagnoserStatistik", filter, diagnoserQueryString)
    }

}
