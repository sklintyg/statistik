/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.entity.mime.MultipartEntityBuilder
import se.inera.statistics.web.service.FilterData
import se.inera.testsupport.RestSupportService

import javax.ws.rs.core.MediaType

import static groovy.json.JsonParserType.CHAR_BUFFER
import static org.apache.http.entity.ContentType.DEFAULT_BINARY

class ReportsUtil {

    static final JSON = MediaType.APPLICATION_JSON
    static final TEXT = MediaType.TEXT_PLAIN
    
    static final VARDGIVARE = "vg1"
    static final VARDGIVARE3 = "vg3"

    final restClient = createClient()
    // CHAR_BUFFER must currently be used in order to eager parse of numbers etc. Ex. 17.0 in JSON ends up as 17 in groovy
    final jsonp = new JsonSlurper().setType(CHAR_BUFFER)

    // for file uploads
    class MultipartBody {
        InputStream file
        String filename
    }

    private RESTClient createClient() {
        def baseUrl = System.getProperty("baseUrl") ?: "http://localhost:8080/"
        def client = new RESTClient(baseUrl, JSON)
        client.ignoreSSLIssues()

        client.encoder[MediaType.MULTIPART_FORM_DATA] = { MultipartBody body ->
            MultipartEntityBuilder.create().addBinaryBody(
                    "file",
                    body.file,
                    DEFAULT_BINARY,
                    body.filename
            ).build()
        }

        client.parser[JSON] = { HttpResponseDecorator resp ->
            jsonp.parse(resp.entity.content)
        }

        client.parser[MediaType.TEXT_HTML] = { HttpResponseDecorator resp ->
            resp.entity.content.text
        }

        return client
    }

    // puts a resource
    def put(name, obj) {
        def response = restClient.put(path: "/api/testsupport/${name}", body: new JsonBuilder(obj).toString())
        assert response.status == 200
    }

    // posts a resource
    def post(name, body) {
        def response = restClient.post(path: "/api/testsupport/${name}", body: body)
        assert response.status == 200
    }

    def post(name) {
        post(name, "")
    }

    def setCurrentDateTime(long timeMillis) {
        post("now", String.valueOf(timeMillis))
    }

    def setCutoff(cutoff) {
        post("cutoff", String.valueOf(cutoff))
    }

    def clearDatabase() {
        post("clearDatabase")
    }

    def insertIntyg(intyg) {
        put("intyg", intyg)
    }

    def insertPersonal(personal) {
        put("personal", personal)
    }

    def processIntyg() {
        post("processIntyg")
    }

    def insertMeddelande(meddelande) {
        put("meddelande", meddelande)
    }

    def processMeddelande() {
        post("processMeddelande")
      }

    def denyCalc() {
        post("denyCalc")
    }

    def allowCalc() {
        post("allowCalc")
    }

    def clearCountyPopulation() {
        post("clearCountyPopulation")
    }

    def insertCountyPopulation(countyPopulation, date) {
        put("countyPopulation/${date}", countyPopulation)
    }

    def sendIntygToMottagare(String intygId, String mottagare) {
        post("sendIntygToMottagare/${intygId}/${mottagare}")
    }

    def getReportAntalSjukfall() {
        return get("/api/getNumberOfCasesPerMonth")
    }

    def getReportAntalIntyg() {
        return get("/api/getIntygPerTyp")
    }

    def getReportAntalIntygPerManad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getTotalNumberOfIntygPerMonth", filter, "vgid=" + vgid)
    }

    def getReportAntalSjukfallInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonth", filter, "vgid=" + vgid)
    }

    def getReportAntalIntygInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getIntygPerTypePerMonth", filter, "vgid=" + vgid)
    }

    def getReportAndelKompletteringar() {
        return get("/api/getAndelKompletteringar")
    }

    def getReportAndelKompletteringarInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getAndelKompletteringar", filter, "vgid=" + vgid)
    }

    def getReportAndelKompletteringarTvarsnittInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getAndelKompletteringarTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportAndelKompletteringarLandsting(String vgid, filter) {
        return get("/api/landsting/getAndelKompletteringarLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

    def getReportAntalIntygLandsting(String vgid, filter) {
        return get("/api/landsting/getIntygPerTypePerMonthLandsting", filter, "vgid=" + vgid)
    }

    def getReportAntalIntygTvarsnittInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getIntygPerTypeTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportAntalMeddelandenVardenhetInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getMeddelandenPerAmnePerEnhet", filter, "vgid=" + vgid)
    }

    def getReportAntalMeddelandenVardenhetTvarsnittInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getMeddelandenPerAmnePerEnhetTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportAntalMeddelandenVardenhetLandsting(String vgid, filter) {
        throw new RuntimeException("Ej implementerat an!")
//        return get("/api/landsting/getMeddelandenPerAmneLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

    def getReportAntalMeddelandenInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getMeddelandenPerAmne", filter, "vgid=" + vgid)
    }

    def getReportAntalMeddelandenTvarsnittInloggad(String vgid, filter) {
        return get(getVerksamhetUrlPrefix() + "/getMeddelandenPerAmneTvarsnitt", filter, "vgid=" + vgid)
    }

    def getReportAntalMeddelandenLandsting(String vgid, filter) {
        return get("/api/landsting/getMeddelandenPerAmneLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

    def getReportAntalMeddelanden() {
        return get("/api/getMeddelandenPerAmne")
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
            println("GET: " + url + "?" + queryWithFilter)
            def response = restClient.get(path: url, queryString : queryWithFilter)
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

    private def isFilterEmpty(FilterData filter) {
        return filter.useDefaultPeriod && filter.diagnoser.isEmpty() && filter.enheter.isEmpty() && filter.sjukskrivningslangd.isEmpty() && filter.aldersgrupp.isEmpty() && filter.intygstyper.isEmpty()
    }

    private def addFilterToQueryStringIfSet(filterQueryName, FilterData filter, queryString) {
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
        def response = restClient.post(path: '/fake', body: [userJsonDisplay:loginData ], requestContentType : "application/x-www-form-urlencoded" )
        assert response.status == 302
        println "Using logindata: ${loginData}"
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

    def getFilterHash(filterData) {
        def filterJsonString = new JsonBuilder(filterData).toString()
        println("Filter to filterhash: " + filterJsonString)
        def response = restClient.post(path: "/api/filter", body: filterJsonString, requestContentType: JSON, contentType: TEXT)
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

    def getReportAntalSjukfallSomTvarsnittInloggad(String vgid, FilterData filter) {
        return get(getVerksamhetUrlPrefix() + "/getNumberOfCasesPerMonthTvarsnitt", filter, "vgid=" + vgid)
    }

    def uploadFile(String vgid, InputStream file, filename) {
        def body = new MultipartBody(file: file, filename: filename);
        try {
            def response = restClient.post(requestContentType: "multipart/form-data", path: '/api/landsting/fileupload', body: body, queryString : "vgid=" + vgid)
            return response.data
        } catch (HttpResponseException e) {
            return e.getResponse().getData()
        }
    }

    def clearLandstingFileUploads() {
        restClient.delete(path: '/api/testsupport/clearLandstingFileUploads')
    }

    def insertLandsting(vgId) {
        def url = "/api/testsupport/landsting/vgid/" + vgId
        println("insertLandsting: " + url)
        restClient.put(path: url)
    }

    def getSocialstyrelsenReport(fromYear, toYear, dxs) {
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

    def getSocialstyrelsenMedianReport(fromYear, toYear, dxs) {
        def queryString = getSocialstyrelseQueryString(fromYear, toYear, dxs)
        return get("/api/testsupport/getSocialstyrelsenMedianReport", FilterData.empty(), queryString, "filter")
    }

    def getSocialstyrelsenStdDevReport(fromYear, toYear, dxs) {
        def queryString = getSocialstyrelseQueryString(fromYear, toYear, dxs)
        return get("/api/testsupport/getSocialstyrelsenStdDevReport", FilterData.empty(), queryString, "filter")
    }

    def getFkReport() {
        return get("/api/testsupport/getFkYearReport")
    }

    def getReportAntalSjukfallLandstingInloggad(vgid, filter) {
        return get("/api/landsting/getNumberOfCasesPerMonthLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

    def getReportSjukfallPerEnhetLandsting(vgid, filter) {
        return get("/api/landsting/getNumberOfCasesPerEnhetLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

    def getReportSjukfallPerListningarPerEnhetLandsting(vgid, filter) {
        return get("/api/landsting/getNumberOfCasesPerPatientsPerEnhetLandsting", filter, "vgid=" + vgid, "landstingfilter")
    }

}

