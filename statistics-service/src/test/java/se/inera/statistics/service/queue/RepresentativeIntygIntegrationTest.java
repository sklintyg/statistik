package se.inera.statistics.service.queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.demo.UtlatandeBuilder;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.report.api.*;
import se.inera.statistics.service.report.listener.AldersGruppListener;
import se.inera.statistics.service.report.listener.SjukfallPerDiagnosgruppListener;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.scheduler.NationellUpdaterJob;

import javax.jms.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:process-log-qm-test.xml" })
@Transactional
@DirtiesContext
public class RepresentativeIntygIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(RepresentativeIntygIntegrationTest.class);

    private JmsTemplate jmsTemplate;
    private List<String> persons = new ArrayList<>();

    @Autowired
    private CasesPerMonth casesPerMonth;
    @Autowired
    private DiagnosisGroups diagnosisGroups;
    @Autowired
    private DiagnosisSubGroups diagnosisSubGroups;
    @Autowired
    private AgeGroups ageGroups;
    @Autowired
    private DegreeOfSickLeave degreeOfSickLeave;
    @Autowired
    private SjukfallslangdGrupp sjukfallslangdGrupp;
    @Autowired
    private VerksamhetOverview verksamhetOverview;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private NationellUpdaterJob nationellUpdaterJob;

    @Before
    public void setup() {
        List<String> personNummers;
        this.jmsTemplate = new JmsTemplate(connectionFactory);
        personNummers = readList("/personnr/testpersoner.log");
        persons.add(personNummers.get(0)); // k 1950
        persons.add(personNummers.get(300)); // k 1960
        persons.add(personNummers.get(500)); // m 1979
        persons.add(personNummers.get(1000)); // k 1990
        persons.add(personNummers.get(2000)); // m 1991
        persons.add(personNummers.get(4000)); // m 1998
    }

    @Test
    public void deliver_document_from_in_queue_to_statistics_repository() throws IOException {
        AldersGruppListener.setMaxCacheSize(1);
        SjukfallPerDiagnosgruppListener.setMaxCacheSize(1);
        UtlatandeBuilder builder = new UtlatandeBuilder("/json/integration/intyg1.json", "Intyg med 1 sjuktal");


        for (TestIntyg intyg : getIntygWithHelsjukAndSingelmanadAndSingelDiagnos(getPerson(0), getPerson(1), getPerson(2))) {
            System.out.println("Intyg: " + intyg);
            simpleSend(builder.build(intyg.personNr, intyg.startDate, intyg.endDate, intyg.vardenhet, intyg.vardgivare, intyg.diagnos, intyg.grads).toString(), "001");
        }

        sleep();

        assertEquals("Verify that all messages have been processed.", 3 , consumer.processBatch());

        nationellUpdaterJob.checkLog();

        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth1 = casesPerMonth.getCasesPerMonth(getVardenhet(0), new Range(getStart(0), getStop(3)));
        LOG.info("CPM data: " + casesPerMonth1);
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonthNationell = casesPerMonth.getCasesPerMonth("nationell", new Range(getStart(0), getStop(3)));
        LOG.info("Nationell CPM data: " + casesPerMonthNationell);

        DiagnosisGroupResponse diagnosisGroups1 = diagnosisGroups.getDiagnosisGroups(getVardenhet(0), new Range(getStart(0), getStop(3)));
        LOG.info("DG data: " + diagnosisGroups1);
        DiagnosisGroupResponse diagnosisGroupsNationell = diagnosisGroups.getDiagnosisGroups("nationell", new Range(getStart(0), getStop(3)));
        LOG.info("Nationell DG data:" + diagnosisGroupsNationell);

        assertEquals(12, casesPerMonth1.getRows().size());
        assertEquals(12, casesPerMonthNationell.getRows().size());

        for (int i = 0; i < 1; i++) {
            assertEquals(2, casesPerMonth1.getRows().get(i).getFemale().intValue());
            assertEquals(1, casesPerMonth1.getRows().get(i).getMale().intValue());
        }
        for (int i = 1; i < 12; i++) {
            assertEquals(0, casesPerMonth1.getRows().get(i).getFemale().intValue());
            assertEquals(0, casesPerMonth1.getRows().get(i).getMale().intValue());
        }
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void simpleSend(final String intyg, final String correlationId) {
        Destination destination = new ActiveMQQueue("intyg.queue");

        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setStringProperty(Receiver.ACTION, Receiver.CREATED);
                message.setStringProperty(Receiver.CERTIFICATE_ID, correlationId);
                return message;
            }
        });
    }

    private List<String> readList(String path) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"));) {
            List<String> list = new ArrayList<>();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getPerson(int id) {
        return persons.get(id);
    }

    private List<TestIntyg> getIntygWithHelsjukAndSingelmanadAndSingelDiagnos(String... personNummers) {
        List<TestIntyg> testIntygs = new ArrayList<>();
        for (String person : personNummers) {
            testIntygs.add(new TestIntyg(person, getGrads(0), getStart(0), getStop(0), getVardenhet(0), getVardgivare(0), getDiagnosis(0)));
        }
        return testIntygs;
    }

    private String getDiagnosis(int i) {
        String[] diagnoser = { "A00" };
        return diagnoser[i];
    }

    private LocalDate getStart(int i) {
        LocalDate[] dates = { new LocalDate("2011-01-10") };
        return dates[i];
    }

    private LocalDate getStop(int i) {
        LocalDate[] dates = { new LocalDate("2011-01-22"), new LocalDate("2011-02-01"), new LocalDate("2011-03-11"), new LocalDate("2011-12-12") };
        return dates[i];
    }

    private String getVardenhet(int i) {
        String[] enheter = { "Vardenhet1" };
        return enheter[i];
    }

    private String getVardgivare(int i) {
        String[] givare = { "Vardgivare" };
        return givare[i];
    }

    private List<String> getGrads(int... grads) {
        List<String> resultList = new ArrayList<>();
        for (int i : grads) {
            resultList.add(Integer.toString(i));
        }
        return resultList;
    }

    public static class TestIntyg {
        private final String personNr;
        private final List<String> grads;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String vardenhet;
        private final String vardgivare;
        private final String diagnos;

        public TestIntyg(String personNr, List<String> grads, LocalDate startDate, LocalDate endDate, String vardenhet, String vardgivare, String diagnos) {
            this.personNr = personNr;
            this.grads = grads;
            this.startDate = startDate;
            this.endDate = endDate;
            this.vardenhet = vardenhet;
            this.vardgivare = vardgivare;
            this.diagnos = diagnos;
        }

        @Override
        public String toString() {
            return "TestIntyg{" +
                    "personNr='" + personNr + '\'' +
                    ", grads=" + grads +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", vardenhet='" + vardenhet + '\'' +
                    ", vardgivare='" + vardgivare + '\'' +
                    ", diagnos='" + diagnos + '\'' +
                    '}';
        }
    }

}
//CHECKSTYLE:ON MagicNumber
