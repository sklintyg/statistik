package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.codehaus.groovy.runtime.MethodClosure
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.inera.statistics.service.report.model.KonField
import se.inera.statistics.web.service.FilterData
import se.inera.testsupport.Intyg
import se.inera.testsupport.Meddelande
import se.inera.testsupport.Personal
import se.inera.testsupport.RestSupportService

import javax.ws.rs.core.MediaType

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT

class ReportsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsUtil.class);
    public static final VARDGIVARE = "vg1"
    public static final VARDGIVARE3 = "vg3"

    def statistik = createClient()

    private RESTClient createClient() {
        String host = System.getProperty("statistics.base.url") ?: "http://localhost:8080/"
        def client = new RESTClient(host, JSON)
        client.ignoreSSLIssues()
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

    def insertMeddelande(Meddelande meddelande) {
        def builder = new JsonBuilder(meddelande)
        def response = statistik.put(path: '/api/testsupport/meddelande', body: builder.toString())
        assert response.status == 200
    }

    def processMeddelande() {
        def response = statistik.post(path: '/api/testsupport/processMeddelande')
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

    def clearCountyPopulation() {
        def response = statistik.post(path: '/api/testsupport/clearCountyPopulation')
        assert response.status == 200
    }

    def insertCountyPopulation(Map<String, KonField> countyPopulation, String date) {
        def builder = new JsonBuilder(countyPopulation)
        def response = statistik.put(path: '/api/testsupport/countyPopulation/' + date, body: builder.toString())
        assert response.status == 200
    }

    def getReportAntalIntyg() {
        return get("/api/getNumberOfCasesPerMonth")
    }

    def getReportAntalIntygPerManad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getTotalNumberOfIntygPerMonth", filter, "vgid=" + vgid)
    }

    def getReportAntalIntygInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonth", filter, "vgid=" + vgid)
    }

    def getReportAntalMeddelandenInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfMeddelandenPerMonth", filter, "vgid=" + vgid)
    }

    def getReportLangaSjukfallInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getLongSickLeavesData", filter, "vgid=" + vgid)
    }

    def getReportLangaSjukfallSomTvarsnittInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getLongSickLeavesTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportSjukfallPerEnhet(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerEnhet", filter, "vgid=" + vgid)
    }

    def getReportEnskiltDiagnoskapitel(String kapitel) {
        return get("/api/getDiagnosavsnittstatistik/" + kapitel)
    }

    private def get(String url, FilterData filter=FilterData.empty(), String queryString="", String filterQueryName="filter") {
        try {
            def queryWithFilter = addFilterToQueryStringIfSet(filterQueryName, filter, queryString)
            println("GET: " + url + " : " + queryWithFilter)
            def response = statistik.get(path: url, queryString : queryWithFilter)
            return response.data;
        } catch (Throwable e) {
            println "e.message: " + e.message
            if ('Service Unavailable' == e.message) {
                println 'error = 503'
                return []
            } else if ('Access is denied' == e.message) {
                    println 'error: Access is denied'
                    return []
            } else if ('Forbidden' == e.message) {
                    println 'error: Forbidden'
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
        return filter.useDefaultPeriod && filter.diagnoser.isEmpty() && filter.enheter.isEmpty() && filter.verksamhetstyper.isEmpty() && filter.sjukskrivningslangd.isEmpty() && filter.aldersgrupp.isEmpty();
    }

    private String addFilterToQueryStringIfSet(filterQueryName, FilterData filter, queryString) {
        if (isFilterEmpty(filter)) {
            return queryString
        }
        def filterHash = getFilterHash(filter)
        def prefixChar = queryString.isEmpty() ? "" : "&"
        return queryString + prefixChar + filterQueryName + "=" + filterHash
    }

    def getReportEnskiltDiagnoskapitelInloggad(String vgid, String kapitel, filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnosavsnittstatistik/" + kapitel, filter, "vgid=" + vgid)
    }

    def getReportEnskiltDiagnoskapitelSomTvarsnittInloggad(String vgid, String kapitel, filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnosavsnittTvarsnitt/" + kapitel, filter, "vgid=" + vgid)
    }

    def getReportDiagnosgruppInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnoskapitelstatistik", filter, "vgid=" + vgid)
    }

    def getReportIntygstypInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getIntygPerTypePerMonth", filter, "vgid=" + vgid)
    }

    def getReportDiagnosgruppSomTvarsnittInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getDiagnosGruppTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportIntygstypTvarsnittInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getIntygPerTypeTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportDiagnosgrupp() {
        return get("/api/getDiagnoskapitelstatistik")
    }

    def getVardgivareForUser(String user) {
        String lCaseUser = user.toLowerCase()
        switch (lCaseUser) {
            case "user1": return VARDGIVARE;
            case "user2": return VARDGIVARE;
            case "user3": return VARDGIVARE3;
            case "user4": return VARDGIVARE;
            case "user5_vg1": return VARDGIVARE;
            case "user5_vg3": return VARDGIVARE3;
            case "user6": return VARDGIVARE3;
            case "user8": return VARDGIVARE;
            default: throw new RuntimeException("Unknown user: " + lCaseUser)
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
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user2"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user2\"," +
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user3"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user3\"," +
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user4"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user4\"," +
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user5_vg1"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user5\"," +
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user5_vg3"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user5\"," +
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user6"] = "{" +
                "\"fornamn\":\"Anna\"," +
                "\"efternamn\":\"Modig\"," +
                "\"hsaId\":\"user6\"," +
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        logins["user8"] = "{" +
                "\"fornamn\":\"Pro cess\"," +
                "\"efternamn\":\"Ledare UtanMuisson\"," +
                "\"hsaId\":\"user8\"," +
                "\"vardgivarIdSomProcessLedare\":[\"" + getVardgivareForUser(user) + "\"]," +
                "\"vardgivarniva\":\"" + vardgivarniva + "\"" +
                "}"
        def loginData = logins[user]
        def response = statistik.post(path: '/fake', body: [ userJsonDisplay:loginData ], requestContentType : "application/x-www-form-urlencoded" )
        assert response.status == 302
        System.out.println("Using logindata: " + loginData)
    }

    def getLoginInfo() {
        return get("/api/login/getLoginInfo")
    }

    def getReportAldersgrupp() {
        return get("/api/getAgeGroupsStatistics")
    }

    def getReportAldersgruppInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getAgeGroupsStatistics", filter, "vgid=" + vgid)
    }

    def getReportAldersgruppSomTidsserieInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getAgeGroupsStatisticsAsTimeSeries", filter, "vgid=" + vgid)
    }

    def getReportSjukskrivningslangd() {
        return get("/api/getSickLeaveLengthData")
    }

    def getReportSjukskrivningslangdInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getSickLeaveLengthData", filter, "vgid=" + vgid)
    }

    def getReportSjukskrivningsgradInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getDegreeOfSickLeaveStatistics", filter, "vgid=" + vgid)
    }

    def getReportSjukskrivningsgradSomTvarsnittInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getDegreeOfSickLeaveTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportSjukskrivningsgrad() {
        return get("/api/getDegreeOfSickLeaveStatistics")
    }

    def getReportDifferentieratIntygandeSomTidsserieInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getDifferentieratIntygandeStatistics", filter, "vgid=" + vgid)
    }

    def getReportDifferentieratIntygandeTvarsnittSomTvarsnittInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getDifferentieratIntygandeTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportLakareAlderOchKonInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getCasesPerDoctorAgeAndGenderStatistics", filter, "vgid=" + vgid)
    }

    def getReportLakarBefattningInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakarbefattning", filter, "vgid=" + vgid)
    }

    def getReportLakarBefattningSomTidsserieInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakarbefattningSomTidsserie", filter, "vgid=" + vgid)
    }

    def getReportSjukfallPerLakareInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerLakare", filter, "vgid=" + vgid)
    }

    def getReportCasesPerSex() {
        return get("/api/getSjukfallPerSexStatistics")
    }

    def getReportCasesPerCounty() {
        return get("/api/getCountyStatistics")
    }

    def getReportJamforDiagnoserInloggad(String vgid, filter, String diagnosHash) {
        return get(getVerksamhetUrlPrefix() + "/getJamforDiagnoserStatistik/" + diagnosHash, filter, "vgid=" + vgid)
    }

    String getFilterHash(FilterData filterData) {
        def filterJsonString = new JsonBuilder(filterData).toString()
        println("Filter to filterhash: " + filterJsonString)
        def response = statistik.post(path: "/api/filter", body: filterJsonString, requestContentType: JSON, contentType: TEXT)
        assert response.status == 200
        return response.data.getText();
    }

    def getVerksamhetsoversikt(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getOverview", filter, "vgid=" + vgid)
    }

    private String getVerksamhetUrlPrefix() {
        return "/api/verksamhet"
    }

    def getNationalOverview() {
        return get("/api/getOverview")
    }

    def getReportSjukfallPerEnhetSomTidsserieInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerEnhetTimeSeries", filter, "vgid=" + vgid)
    }

    def getReportJamforDiagnoserSomTidsserieInloggad(String vgid, FilterData filterData, String diagnosHash) {
        return get(getVerksamhetUrlPrefix() + "/getJamforDiagnoserStatistikTidsserie/" + diagnosHash, filterData, "vgid=" + vgid)
    }

    def getReportSjukskrivningslangdSomTidsserieInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getSickLeaveLengthTimeSeries", filter, "vgid=" + vgid)
    }

    def getReportSjukfallPerLakareSomTidsserieInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getSjukfallPerLakareSomTidsserie", filter, "vgid=" + vgid)
    }

    def getReportLakareAlderOchKonSomTidsserieInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getCasesPerDoctorAgeAndGenderTimeSeriesStatistics", filter, "vgid=" + vgid)
    }

    def getReportAntalIntygSomTvarsnittInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonthTvarsnitt", filter, "vgid=" + vgid)
    }

    def uploadFile(String vgid, InputStream file, filename) {
        def body = new MultipartBody(file: file, filename: filename);
        try {
        def response = statistik.post(requestContentType: "multipart/form-data", path: '/api/landsting/fileupload', body: body, queryString : "vgid=" + vgid)
        return response.data
        } catch (HttpResponseException e) {
            return e.getResponse().getData()
        }
    }

    def clearLandstingFileUploads() {
        statistik.delete(path: '/api/testsupport/clearLandstingFileUploads')
    }

    def insertLandsting(vgId) {
        def url = "/api/testsupport/landsting/vgid/" + vgId
        println("insertLandsting: " + url)
        statistik.put(path: url)
    }

    def getSocialstyrelsenReport(Integer fromYear, Integer toYear, List<String> dxs) {
        def queryString = getSocialstyrelseQueryString(fromYear, toYear, dxs)
        return get("/api/testsupport/getSocialstyrelsenReport", FilterData.empty(), queryString, "filter")
    }

    private String getSocialstyrelseQueryString(Integer fromYear, Integer toYear, List<String> dxs) {
        def qs = new StringBuilder();
        if (fromYear != null) {
            qs.append("&" + RestSupportService.SOC_PARAM_FROMYEAR + "=" + fromYear);
        }
        if (toYear != null) {
            qs.append("&" + RestSupportService.SOC_PARAM_TOYEAR + "=" + toYear);
        }
        if (dxs != null) {
            for (String dx : dxs) {
                qs.append("&" + RestSupportService.SOC_PARAM_DX + "=" + dx);
            }
        }
        def queryString = qs.replaceFirst("&", "")
        queryString
    }

    def getSocialstyrelsenMedianReport(Integer fromYear, Integer toYear, List<String> dxs) {
        def queryString = getSocialstyrelseQueryString(fromYear, toYear, dxs)
        return get("/api/testsupport/getSocialstyrelsenMedianReport", FilterData.empty(), queryString, "filter")
    }

    def getSocialstyrelsenStdDevReport(Integer fromYear, Integer toYear, List<String> dxs) {
        def queryString = getSocialstyrelseQueryString(fromYear, toYear, dxs)
        return get("/api/testsupport/getSocialstyrelsenStdDevReport", FilterData.empty(), queryString, "filter")
    }

    def getFkReport() {
        return get("/api/testsupport/getFkYearReport")
    }

    def getReportAntalIntygLandstingInloggad(String vgid, filter) {
        return get("/api/landsting/getNumberOfCasesPerMonthLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

    def getReportSjukfallPerEnhetLandsting(String vgid, filter) {
        return get("/api/landsting/getNumberOfCasesPerEnhetLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

    def getReportSjukfallPerListningarPerEnhetLandsting(String vgid, filter) {
        return get("/api/landsting/getNumberOfCasesPerPatientsPerEnhetLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

}

