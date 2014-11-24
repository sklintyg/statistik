import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.internal.JsonReader
import org.joda.time.DateTimeUtils
import org.junit.Ignore
import org.junit.Test
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.web.reports.ReportsUtil

import se.inera.testsupport.Intyg

class ReportsUtilTest {

    @Ignore
    void builderIsWorking() {
        ReportsUtil reportsUtil = new ReportsUtil()

        String fileContents = new File('src/main/resources/intyg1.json').getText('UTF-8')
        Intyg intyg = new Intyg(EventType.CREATED, fileContents, "1", DateTimeUtils.currentTimeMillis())
        reportsUtil.insertIntyg(intyg)

        long now = reportsUtil.getCurrentDateTime()
        println(now)

        reportsUtil.setCurrentDateTime(1416564276848)

        reportsUtil.clearDatabase()

        reportsUtil.processIntyg()
    }

    @Test
    void jsonPathIsWorking() {
        String json = new File('src/test/resources/intyg1.json').getText('UTF-8')

        DocumentContext parsedJson = new JsonReader().parse(json)
        setDiagnosis(parsedJson, "H45")
        addNedsattningsGrad(parsedJson, "100")
        System.out.println(parsedJson.json())
    }

    private void addNedsattningsGrad(DocumentContext parsedJson, String percentage) {
        String nedsattningsblock = "{ quantity:${percentage}, unit:percent }"
        parsedJson.add("\$.observationer[?(@.observationskod.code == '302119000')].varde", nedsattningsblock)
    }

    private void setDiagnosis(DocumentContext parsedJson, String diagnosis) {
        parsedJson.set("\$.observationer[*].observationskod[?(@.codeSystemName == 'ICD-10')].code", diagnosis)
    }

}
