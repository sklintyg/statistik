package se.inera.statistics.web.reports

import groovy.json.JsonBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
//import se.inera.testsupport.Intyg

class ReportsUtil {
    def statistik = new RESTClient('http://localhost:8080/', ContentType.JSON)

    long getCurrentDateTime() {
        def response = statistik.get(path: '/api/testsupport/now')
        response.data;
    }

    def setCurrentDateTime(long timeMillis) {
        def response = statistik.post(path: '/api/testsupport/now', body: String.valueOf(timeMillis))
        assert response.status == 200
    }

    def clearDatabase() {
        def response = statistik.post(path: '/api/testsupport/clearDatabase')
        assert response.status == 200
    }

//    def insertIntyg(Intyg intyg) {
//        def builder = new JsonBuilder(intyg)
//        def response = statistik.put(path: '/api/testsupport/intyg', body: builder.toString())
//        assert response.status == 200
//    }
//
    def processIntyg() {
        def response = statistik.post(path: '/api/testsupport/processIntyg')
        assert response.status == 200
    }

}
