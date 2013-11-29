package se.inera.statistics.queue

import org.joda.time.LocalDate
import se.inera.statistics.context.StartUp
import se.inera.statistics.service.demo.UtlatandeBuilder
import se.inera.statistics.service.helper.QueueHelper
import se.inera.statistics.service.helper.TestData
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.service.report.model.Range

class IntygSender {
    static Map<String, TestData> testResult
    UtlatandeBuilder builder1 = new UtlatandeBuilder("/json/integration/intyg1.json", "Intyg med 1 sjuktal")
    UtlatandeBuilder builder2 = new UtlatandeBuilder("/json/integration/intyg2.json", "Intyg med 2 sjuktal")
    UtlatandeBuilder builder3 = new UtlatandeBuilder("/json/integration/intyg3.json", "Intyg med 3 sjuktal")
    UtlatandeBuilder builder4 = new UtlatandeBuilder("/json/integration/intyg4.json", "Intyg med 4 sjuktal")
    UtlatandeBuilder[] builders = [builder1, builder2, builder3, builder4]

    boolean sendData(String file) {
        QueueHelper bean = (QueueHelper) StartUp.context.getBean("se.inera.statistics.service.helper.QueueHelper")
        bean.enqueueFromFile(builders, file)
        true
    }

    boolean sendIntyg(String person, String diagnos, String start1, String stop1, String grad1, String start2, String stop2, String grad2, String start3, String stop3, String grad3, String start4, String stop4, String grad4, String enhet, String vardgivare, String trackingId) {
        sendIntyg(EventType.CREATED.name(), person, diagnos, start1, stop1, grad1, start2, stop2, grad2, start3, stop3, grad3, start4, stop4, grad4, enhet, vardgivare, trackingId)
    }

    boolean sendIntyg(String typ, String person, String diagnos, String start1, String stop1, String grad1, String start2, String stop2, String grad2, String start3, String stop3, String grad3, String start4, String stop4, String grad4, String enhet, String vardgivare, String trackingId) {
        QueueHelper bean = (QueueHelper) StartUp.context.getBean("se.inera.statistics.service.helper.QueueHelper")
        List<LocalDate> starts = new ArrayList<>()
        List<LocalDate> stops = new ArrayList<>()
        List<String> grads = new ArrayList<>()
        if (!"".equals(start1)) {
            starts.add(new LocalDate(start1))
            stops.add(new LocalDate(stop1))
            grads.add(toGrad(grad1))
        }
        if (!"".equals(start2)) {
            starts.add(new LocalDate(start2))
            stops.add(new LocalDate(stop2))
            grads.add(toGrad(grad2))
        }
        if (!"".equals(start3)) {
            starts.add(new LocalDate(start3));
            stops.add(new LocalDate(stop3));
            grads.add(toGrad(grad3))
        }
        if (!"".equals(start4)) {
            starts.add(new LocalDate(start4));
            stops.add(new LocalDate(start4));
            grads.add(toGrad(grad4))
        }
        if (starts.size() < 1) {
            false
        } else {
            bean.enqueue(builders[starts.size() - 1], typ, person, diagnos, starts, stops, grads, enhet, vardgivare, trackingId)
            true
        }
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
        QueueHelper bean = (QueueHelper) StartUp.context.getBean("se.inera.statistics.service.helper.QueueHelper")
        testResult = bean.printAndGetPersistedData(enhet1, enhet2, new Range(new LocalDate(start), new LocalDate(stop)))
        true
    }

    String toGrad(String s) {
        if (s.endsWith("%")) {
            s = s.substring(0, s.size() - 1);
        }
        int i = 100 - Integer.parseInt(s);
        return "" + i;
    }

    static Map<String, TestData> getTestResult() {
        return testResult
    }
}
