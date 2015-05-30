package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpEntity
import org.codehaus.groovy.runtime.MethodClosure
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.inera.statistics.web.service.FilterData
import se.inera.testsupport.Intyg
import se.inera.testsupport.Personal
import org.apache.http.entity.mime.MultipartEntityBuilder

import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.content.InputStreamBody

import javax.ws.rs.core.MediaType

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT

class ReportsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsUtil.class);
    public static final VARDGIVARE = "vg1"
    public static final VARDGIVARE3 = "vg3"
    public static final String HOST = 'http://localhost:8080/'

    def statistik = createClient()

    private RESTClient createClient() {
        def client = new RESTClient(HOST, JSON)
        client.encoder.putAt(MediaType.MULTIPART_FORM_DATA, new MethodClosure(this, 'encodeMultiPart'));
        return client
    }

    HttpEntity encodeMultiPart(MultipartBody body) {
        return MultipartEntityBuilder.create()
                .addBinaryBody(
                'file',
                body.file,
                org.apache.http.entity.ContentType.MULTIPART_FORM_DATA,
                body.filename
        ).build()
    }

    class MultipartBody {
        InputStream file
        String filename
    }

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
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonth", filter)
    }

    def getReportLangaSjukfallInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getLongSickLeavesData", filter)
    }

    def getReportLangaSjukfallSomTvarsnittInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getLongSickLeavesTvarsnitt", filter)
    }

    def getReportSjukfallPerEnhet(filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerEnhet", filter)
    }

    def getReportEnskiltDiagnoskapitel(String kapitel) {
        return get("/api/getDiagnosavsnittstatistik/" + kapitel)
    }

    private def get(String url, FilterData filter=FilterData.empty(), String queryString="") {
        try {
            def queryWithFilter = addFilterToQueryStringIfSet(filter, queryString)
            println("GET: " + url + " : " + queryWithFilter)
            def response = statistik.get(path: url, queryString : queryWithFilter)
            assert response.status == 200
            return response.data;
        } catch (HttpResponseException e) {
            if ('Service Unavailable' == e.message) {
                println 'error = 503'
                return []
            } else {
                throw e
            }
        }
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
        def filterHash = getFilterHash(filter)
        def prefixChar = queryString.isEmpty() ? "" : "&"
        return queryString + prefixChar + "filter=" + filterHash
    }

    def getReportEnskiltDiagnoskapitelInloggad(String kapitel, filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnosavsnittstatistik/" + kapitel, filter)
    }

    def getReportEnskiltDiagnoskapitelSomTvarsnittInloggad(String kapitel, filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnosavsnittTvarsnitt/" + kapitel, filter)
    }

    def getReportDiagnosgruppInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnoskapitelstatistik", filter)
    }

    def getReportDiagnosgruppSomTvarsnittInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnosGruppTvarsnitt", filter)
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
            case "enhet4": return VARDGIVARE3;
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
        logins["user6"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user6\"," +
                "\"enhetId\":\"enhet4\"," +
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
        return get(getVerksamhetUrlPrefix() + "/getAgeGroupsStatistics", filter)
    }

    def getReportAldersgruppSomTidsserieInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getAgeGroupsStatisticsAsTimeSeries", filter)
    }

    def getReportSjukskrivningslangd() {
        return get("/api/getSickLeaveLengthData")
    }

    def getReportSjukskrivningslangdInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getSickLeaveLengthData", filter)
    }

    def getReportSjukskrivningsgradInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getDegreeOfSickLeaveStatistics", filter)
    }

    def getReportSjukskrivningsgradSomTvarsnittInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getDegreeOfSickLeaveTvarsnitt", filter)
    }

    def getReportSjukskrivningsgrad() {
        return get("/api/getDegreeOfSickLeaveStatistics")
    }

    def getReportLakareAlderOchKonInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getCasesPerDoctorAgeAndGenderStatistics", filter)
    }

    def getReportLakarBefattningInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakarbefattning", filter)
    }

    def getReportLakarBefattningSomTidsserieInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakarbefattningSomTidsserie", filter)
    }

    def getReportSjukfallPerLakareInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakare", filter)
    }

    def getReportCasesPerSex() {
        return get("/api/getSjukfallPerSexStatistics")
    }

    def getReportCasesPerCounty() {
        return get("/api/getCountyStatistics")
    }

    def getReportJamforDiagnoserInloggad(filter, String diagnosHash) {
        return get(getVerksamhetUrlPrefix() + "/getJamforDiagnoserStatistik/" + diagnosHash, filter)
    }

    String getFilterHash(FilterData filterData) {
        def filterJsonString = new JsonBuilder(filterData).toString()
        println("Filter to filterhash: " + filterJsonString)
        def response = statistik.post(path: "/api/filter", body: filterJsonString, requestContentType: JSON, contentType: TEXT)
        assert response.status == 200
        return response.data.getText();
    }

    def getVerksamhetsoversikt(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getOverview", filter)
    }

    private String getVerksamhetUrlPrefix() {
        return "/api/verksamhet"
    }

    def getNationalOverview() {
        return get("/api/getOverview")
    }

    def getReportSjukfallPerEnhetSomTidsserieInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerEnhetTimeSeries", filter)
    }

    def getReportJamforDiagnoserSomTidsserieInloggad(FilterData filterData, String diagnosHash) {
        return get(getVerksamhetUrlPrefix() + "/getJamforDiagnoserStatistikTidsserie/" + diagnosHash, filterData)
    }

    def getReportSjukskrivningslangdSomTidsserieInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getSickLeaveLengthTimeSeries", filter)
    }

    def getReportSjukfallPerLakareSomTidsserieInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getSjukfallPerLakareSomTidsserie", filter)
    }

    def getReportLakareAlderOchKonSomTidsserieInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getCasesPerDoctorAgeAndGenderTimeSeriesStatistics", filter)
    }

    def getReportAntalIntygSomTvarsnittInloggad(FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonthTvarsnitt", filter)
    }

    def uploadFile(InputStream file, filename) {
        def body = new MultipartBody(file: file, filename: filename);
        try {
        def response = statistik.post(requestContentType: "multipart/form-data", path: getVerksamhetUrlPrefix() + '/landsting/fileupload', body: body)
        return response.data
        } catch (HttpResponseException e) {
            return e.getResponse().getData()
        }
    }

    def insertLandsting(vgId) {
        def url = "/api/testsupport/landsting/name/" + vgId + "/vgid/" + vgId
        println("insertLandsting: " + url)
        statistik.put(path: url)
    }

    def getReportAntalIntygLandstingInloggad(filter) {
        return get(getVerksamhetUrlPrefix() + "/landsting/getNumberOfCasesPerMonthLandsting", filter)
    }

    def getReportSjukfallPerEnhetLandsting(filter) {
        return get(getVerksamhetUrlPrefix() + "/landsting/getNumberOfCasesPerEnhetLandsting", filter)
    }

    def getReportSjukfallPerListningarPerEnhetLandsting(filter) {
        return get(getVerksamhetUrlPrefix() + "/landsting/getNumberOfCasesPerPatientsPerEnhetLandsting", filter)
    }

}

