package se.inera.statistics.queue;

import org.junit.Test;
import se.inera.statistics.context.StartUp;
import se.inera.statistics.service.helper.TestData;

import java.util.Map;

public class IntygSenderTest {

    StartUp startUp = new StartUp();
    IntygSender intygSender;

    @Test
    public void verifySendData() {
        try {
            startUp.startContext("fitnesse-context.xml");
            intygSender = new IntygSender();
            intygSender.sendData("/testfall.csv");
            intygSender.sleep(5000);
            intygSender.getResult("ENVE", "TVAVE", "2012-01-01", "2013-11-01");
            Map<String, TestData> resultList = IntygSender.getTestResult();

        } finally {
            startUp.stopContext();
        }
    }

    @Test
    public void verifySendIntyg() {
        try {
            startUp.startContext("fitnesse-context.xml");
            intygSender = new IntygSender();
            intygSender.sendIntyg("19790407-9295", "G01", "2013-02-05", "2012-09-06", "50", "","","", "","" ,"","","","","ENVE", "EnVG", "1");
            intygSender.sleep(5000);
            intygSender.getResult("ENVE", "TVAVE", "2012-01-01", "2013-11-01");
            Map<String, TestData> resultList = IntygSender.getTestResult();

        } finally {
            startUp.stopContext();
        }
    }
}
