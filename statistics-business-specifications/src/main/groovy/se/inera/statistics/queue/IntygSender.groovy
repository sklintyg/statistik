package se.inera.statistics.queue

import org.joda.time.LocalDate
import se.inera.statistics.context.StartUp
import se.inera.statistics.service.demo.UtlatandeBuilder
import se.inera.statistics.service.helper.QueueHelper
import se.inera.statistics.service.helper.TestData
import se.inera.statistics.service.report.model.Range

class IntygSender {
    static Map<String, TestData> testResult

    boolean sendData(String file) {
        UtlatandeBuilder builder1 = new UtlatandeBuilder("/json/integration/intyg1.json", "Intyg med 1 sjuktal")
        UtlatandeBuilder builder2 = new UtlatandeBuilder("/json/integration/intyg2.json", "Intyg med 2 sjuktal")
        UtlatandeBuilder builder3 = new UtlatandeBuilder("/json/integration/intyg3.json", "Intyg med 3 sjuktal")
        UtlatandeBuilder builder4 = new UtlatandeBuilder("/json/integration/intyg4.json", "Intyg med 4 sjuktal")
        UtlatandeBuilder[] builders = [ builder1, builder2, builder3, builder4 ]
        QueueHelper bean = (QueueHelper)StartUp.context.getBean("se.inera.statistics.service.helper.QueueHelper")
        bean.enqueueFromFile(builders, file)
        true
    }

    boolean sleep(int amount) {
        try {
            Thread.sleep(amount)
        } catch (InterruptedException e) {
            e.printStackTrace()
        }
        true
    }

    boolean getResult(enhet1, enhet2, String start, String stop) {
        QueueHelper bean = (QueueHelper)StartUp.context.getBean("se.inera.statistics.service.helper.QueueHelper")
        testResult = bean.printAndGetPersistedData(enhet1, enhet2, new Range(new LocalDate(start), new LocalDate(stop)))
        true
    }

    static Map<String, TestData> getTestResult() {
        return testResult
    }
}
