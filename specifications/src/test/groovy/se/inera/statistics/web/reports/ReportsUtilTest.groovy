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

}
