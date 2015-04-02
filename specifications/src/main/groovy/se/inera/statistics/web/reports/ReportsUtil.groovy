package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.inera.statistics.web.service.FilterData
import se.inera.testsupport.Intyg
import se.inera.testsupport.Personal

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT

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

    def denyCalc() {
        def response = statistik.post(path: '/api/testsupport/denyCalc')
        assert response.status == 200
    }

    def allowCalc() {
        def response = statistik.post(path: '/api/testsupport/allowCalc')
        assert response.status == 200
    }

    def getReportAntalIntyg() {
        return get("/api/getNumberOfCasesPerMonth")
    }

    def getReportAntalIntygInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonth", filter)
    }

    def getReportLangaSjukfallInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getLongSickLeavesData", filter)
    }

    def getReportSjukfallPerEnhet(filter) {
        return post(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerEnhet", filter)
    }

    def getReportEnskiltDiagnoskapitel(String kapitel) {
        return get("/api/getDiagnosavsnittstatistik/" + kapitel)
    }

    private def get(String url) {
        def response = statistik.get(path: url)
        assert response.status == 200
        return response.data;
    }

    private def post(String url, FilterData filter=FilterData.empty(), String queryString="", String bodyString="") {
        def queryWithFilter = addFilterToQueryStringIfSet(filter, queryString)
        println("Calling url: " + url + " with query: " + queryWithFilter + " and body: " + bodyString)
        try {
            def response = statistik.post(path: url, body: bodyString, requestContentType: JSON, queryString : queryWithFilter)
            assert response.status == 200
            println 'data =' + response.data
            return response.data
        } catch (HttpResponseException e) {
            if ('Service Unavailable' == e.message) {
                println 'error = 503'
                return []
            } else {
                throw e
            }
        }
    }

    private boolean isFilterEmpty(FilterData filter) {
        return filter.diagnoser.isEmpty() && filter.enheter.isEmpty() && filter.verksamhetstyper.isEmpty();
    }

    private String addFilterToQueryStringIfSet(FilterData filter, queryString) {
        if (isFilterEmpty(filter)) {
            return queryString
        }
        def filterHash = getFilterHash(filter.enheter, filter.verksamhetstyper, filter.diagnoser)
        def prefixChar = queryString.isEmpty() ? "" : "&"
        return queryString + prefixChar + "filter=" + filterHash
    }

    def getReportEnskiltDiagnoskapitelInloggad(String kapitel, filter) {
        return post(getVerksamhetUrlPrefix() + "/getDiagnosavsnittstatistik/" + kapitel, filter)
    }

    def getReportDiagnosgruppInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getDiagnoskapitelstatistik", filter)
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
            case "user5_vg1": return VARDGIVARE;
            case "user5_vg3": return VARDGIVARE3;
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
        logins["user5_vg1"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user5\"," +
                "\"enhetId\":\"enhet1\"," +
                "\"vardgivarId\":\"" + getVardgivareForUser(user) + "\"," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user5_vg3"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user5\"," +
                "\"enhetId\":\"enhet3\"," +
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

    def getReportAldersgruppInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getAgeGroupsStatistics", filter)
    }

    def getReportAldersgruppSomTidsserieInloggad(FilterData filter) {
        return post(getVerksamhetUrlPrefix() + "/getAgeGroupsStatisticsAsTimeSeries", filter)
    }

    def getReportSjukskrivningslangd() {
        return get("/api/getSickLeaveLengthData")
    }

    def getReportSjukskrivningslangdInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getSickLeaveLengthData", filter)
    }

    def getReportAldersgruppPagaendeInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getAgeGroupsCurrentStatistics", filter)
    }

    def getReportSjukskrivningslangdPagaendeInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getSickLeaveLengthCurrentData", filter)
    }

    def getReportSjukskrivningsgradInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getDegreeOfSickLeaveStatistics", filter)
    }

    def getReportSjukskrivningsgrad() {
        return get("/api/getDegreeOfSickLeaveStatistics")
    }

    def getReportLakareAlderOchKonInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getCasesPerDoctorAgeAndGenderStatistics", filter)
    }

    def getReportLakarBefattningInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakarbefattning", filter)
    }

    def getReportLakarBefattningSomTidsserieInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakarbefattningSomTidsserie", filter)
    }

    def getReportSjukfallPerLakareInloggad(filter) {
        return post(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakare", filter)
    }

    def getReportCasesPerSex() {
        return get("/api/getSjukfallPerSexStatistics")
    }

    def getReportCasesPerCounty() {
        return get("/api/getCountyStatistics")
    }

    def getReportJamforDiagnoserInloggad(filter, String diagnosHash) {
        return post(getVerksamhetUrlPrefix() + "/getJamforDiagnoserStatistik/" + diagnosHash, filter)
    }

    String getFilterHash(enheter, verksamhetstyper, diagnoser) {
        def filterData = new FilterData(diagnoser, enheter, verksamhetstyper)
        def filterJsonString = new JsonBuilder(filterData).toString()
        println("Filter to filterhash: " + filterJsonString)
        def response = statistik.post(path: "/api/filter", body: filterJsonString, requestContentType: JSON, contentType: TEXT)
        assert response.status == 200
        return response.data.getText();
    }

    def getVerksamhetsoversikt(FilterData filter) {
        return post(getVerksamhetUrlPrefix() + "/getOverview", filter)
    }

    private String getVerksamhetUrlPrefix() {
        return "/api/verksamhet"
    }

    def getNationalOverview() {
        return get("/api/getOverview")
    }

    def getReportSjukfallPerEnhetSomTidsserieInloggad(FilterData filter) {
        return post(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerEnhetTimeSeries", filter)
    }

    def getReportJamforDiagnoserSomTidsserieInloggad(FilterData filterData, String diagnosHash) {
        return post(getVerksamhetUrlPrefix() + "/getJamforDiagnoserStatistikTidsserie/" + diagnosHash, filterData)
    }

    def getReportSjukskrivningslangdSomTidsserieInloggad(FilterData filter) {
        return post(getVerksamhetUrlPrefix() + "/getSickLeaveLengthTimeSeries", filter)
    }

    def getReportSjukfallPerLakareSomTidsserieInloggad(FilterData filter) {
        return post(getVerksamhetUrlPrefix() + "/getSjukfallPerLakareSomTidsserie", filter)
    }

    def getReportLakareAlderOchKonSomTidsserieInloggad(FilterData filter) {
        return post(getVerksamhetUrlPrefix() + "/getCasesPerDoctorAgeAndGenderTimeSeriesStatistics", filter)
    }

    def getReportAntalIntygSomTvarsnittInloggad(FilterData filter) {
        return post(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonthTvarsnitt", filter)
    }

}
