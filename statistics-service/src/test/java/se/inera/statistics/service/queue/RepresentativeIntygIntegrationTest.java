package se.inera.statistics.service.queue;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.demo.UtlatandeBuilder;
import se.inera.statistics.service.helper.QueueHelper;
import se.inera.statistics.service.helper.QueueSender;
import se.inera.statistics.service.helper.TestData;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.report.api.*;
import se.inera.statistics.service.report.api.FallPerLan;
import se.inera.statistics.service.report.listener.AldersGruppListener;
import se.inera.statistics.service.report.listener.SjukfallPerDiagnosgruppListener;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.scheduler.NationellUpdaterJob;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:process-log-qm-test.xml" })
@Transactional
@DirtiesContext
public class RepresentativeIntygIntegrationTest {
    private static final int PERSON_K1950 = 0;
    private static final int PERSON_K1960 = 1;
    private static final int PERSON_M1979 = 2;

    private static final Logger LOG = LoggerFactory.getLogger(RepresentativeIntygIntegrationTest.class);
    public static final String G01 = "G01";
    public static final String L99 = "L99";
    public static final String A00 = "A00";
    public static final int ENVE = 0;
    public static final int TVAVE = 1;

    private List<String> persons = new ArrayList<>();

    @Autowired
    private CasesPerMonth casesPerMonth;
    @Autowired
    private Diagnosgrupp diagnosgrupp;
    @Autowired
    private Diagnoskapitel diagnoskapitel;
    @Autowired
    private Aldersgrupp aldersgrupp;
    @Autowired
    private Sjukskrivningsgrad sjukskrivningsgrad;
    @Autowired
    private SjukfallslangdGrupp sjukfallslangdGrupp;
    @Autowired
    private VerksamhetOverview verksamhetOverview;
    @Autowired
    private Overview overview;
    @Autowired
    private FallPerLan fallPerLan;

    @Autowired
    private QueueHelper queueHelper;
    @Autowired
    private QueueSender queueSender;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private NationellUpdaterJob nationellUpdaterJob;

    @Before
    public void setup() {
        List<String> personNummers;
        personNummers = readList("/personnr/testpersoner.log");
        persons.add(personNummers.get(0)); // k 1950
        persons.add(personNummers.get(300)); // k 1960
        persons.add(personNummers.get(500)); // m 1979
        persons.add(personNummers.get(1000)); // k 1990
        persons.add(personNummers.get(2000)); // m 1991
        persons.add(personNummers.get(5500)); // m 1998
    }

    @Test
    public void deliver_document_from_in_queue_to_statistics_repository() throws IOException {
        AldersGruppListener.setMaxCacheSize(1);
        SjukfallPerDiagnosgruppListener.setMaxCacheSize(1);
        UtlatandeBuilder builder = new UtlatandeBuilder("/json/integration/intyg1.json", "Intyg med 1 sjuktal");

        LOG.info("===========START==========");
        LOG.info("3 persons, 1 intyg each, basic test.");

        LOG.info("===========INPUT==========");
        for (TestIntyg intyg : getIntygWithHelsjukAndSingelmanadAndSingelDiagnos(getPerson(PERSON_K1950), getPerson(PERSON_K1960), getPerson(PERSON_M1979))) {
            LOG.info("Intyg: " + intyg);
            queueSender.simpleSend(builder.build(intyg.personNr, intyg.startDate, intyg.endDate, intyg.vardenhet, intyg.vardgivare, intyg.diagnos, intyg.grads).toString(),
                    "001");
        }

        sleep();

        assertEquals("Verify that all messages have been processed.", 3, consumer.processBatch());

        nationellUpdaterJob.checkLog();

        LOG.info("===========RESULT=========");
        Map<String, TestData> result = queueHelper.printAndGetPersistedData(getVardenhet(ENVE), getVardenhet(TVAVE), new Range(getStart(0), getStop(3)));

        LOG.info("============END===========\n");

        result.get("casesPerMonth1").getReplyObject();
        result.get("casesPerMonthNationell").getReplyObject();
    }

    @Test
    public void deliver_document_from_in_queue_to_statistics_repository_with_usecase_data() throws IOException {
        AldersGruppListener.setMaxCacheSize(1);
        SjukfallPerDiagnosgruppListener.setMaxCacheSize(1);
        UtlatandeBuilder builder = new UtlatandeBuilder("/json/integration/intyg1.json", "Intyg med 1 sjuktal");

        LOG.info("===========START==========");
        LOG.info("Grundtest");

        LOG.info("===========INPUT==========");
        List<String> persons = Arrays.asList(getPerson(PERSON_K1950), getPerson(PERSON_K1960), getPerson(PERSON_M1979));
        List<String> diagnosKods = Arrays.asList(G01, L99, A00, "F99", "R00", "Z00", "M00", "P00", "G02");
        List<LocalDate> start = Arrays.asList(new LocalDate("2012-02-05"), new LocalDate("2012-03-01"), new LocalDate("2012-02-05"),
                new LocalDate("2012-03-01"), new LocalDate("2013-02-05"), new LocalDate("2013-01-01"), new LocalDate("2013-02-05"),
                new LocalDate("2013-01-01"), new LocalDate("2013-02-05"));
        List<LocalDate> stop = Arrays.asList(new LocalDate("2012-02-06"), new LocalDate("2013-03-31"), new LocalDate("2012-02-07"),
                new LocalDate("2013-01-29"), new LocalDate("2013-02-08"), new LocalDate("2013-01-30"), new LocalDate("2013-02-09"),
                new LocalDate("2013-01-31"), new LocalDate("2013-02-10"));
        List<String> vardgivares = Arrays.asList("EnVG", "TvaVG", "EnVG", "TvaVG", "EnVG", "TvaVG", "EnVG", "TvaVG", "EnVG");
        List<String> vardenhet = Arrays.asList(getVardenhet(ENVE), getVardenhet(TVAVE), getVardenhet(ENVE), getVardenhet(TVAVE), getVardenhet(ENVE),
                getVardenhet(TVAVE), getVardenhet(ENVE), getVardenhet(TVAVE), getVardenhet(ENVE));
        List<TestIntyg> intygs = getIntygWithHelsjukAndSingelmanadAndDiagnosList(persons, diagnosKods, start, stop, vardgivares, vardenhet);
        for (TestIntyg intyg : intygs) {
            LOG.info("Intyg: " + intyg);
            queueSender.simpleSend(builder.build(intyg.personNr, intyg.startDate, intyg.endDate, intyg.vardenhet, intyg.vardgivare, intyg.diagnos, intyg.grads).toString(),
                    "001");
        }

        sleep();

        assertEquals("Verify that all messages have been processed.", 27, consumer.processBatch());

        nationellUpdaterJob.checkLog();

        LOG.info("===========RESULT=========");
        queueHelper.printAndGetPersistedData(getVardenhet(ENVE), getVardenhet(TVAVE), new Range(getStart(0), getStop(3)));

        LOG.info("============END===========\n");
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<String> readList(String path) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"))) {
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
            testIntygs.add(new TestIntyg(person, getGrads(0), getStart(1), getStop(1), getVardenhet(0), getVardgivare(0), getDiagnosis(0)));
        }
        return testIntygs;
    }

    private List<TestIntyg> getIntygWithHelsjukAndSingelmanadAndDiagnosList(List<String> personNummers, List<String> diagnosKods, List<LocalDate> starts,
            List<LocalDate> stops, List<String> vardgivares, List<String> vardenhets) {
        List<TestIntyg> testIntygs = new ArrayList<>();
        for (String person : personNummers) {
            for (int i = 0; i < diagnosKods.size(); i++) {
                String diagnosKod = diagnosKods.get(i);
                LocalDate start = starts.get(i);
                LocalDate stop = stops.get(i);
                String vardgivare = vardgivares.get(i);
                String vardenhet = vardenhets.get(i);
                testIntygs.add(new TestIntyg(person, getGrads(0), start, stop, vardenhet, vardgivare, diagnosKod));

            }
        }
        return testIntygs;
    }

    private String getDiagnosis(int i) {
        String[] diagnoser = {"A00"};
        return diagnoser[i];
    }

    private LocalDate getStart(int i) {
        LocalDate[] dates = {new LocalDate("2012-02-01"), new LocalDate("2011-01-11")};
        return dates[i];
    }

    private LocalDate getStop(int i) {
        LocalDate[] dates = {new LocalDate("2012-01-22"), new LocalDate("2011-02-21"), new LocalDate("2011-03-11"), new LocalDate("2013-11-17")};
        return dates[i];
    }

    private String getVardenhet(int i) {
        String[] enheter = {"ENVE", "TVAVE"};
        return enheter[i];
    }

    private String getVardgivare(int i) {
        String[] givare = {"Vardgivare"};
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
            return "TestIntyg{" + "personNr='" + personNr + '\'' + ", grads=" + grads + ", startDate=" + startDate + ", endDate=" + endDate + ", vardenhet='"
                    + vardenhet + '\'' + ", vardgivare='" + vardgivare + '\'' + ", diagnos='" + diagnos + '\'' + '}';
        }
    }

}
// CHECKSTYLE:ON MagicNumber
