package se.inera.statistics.service.queue;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.activemq.command.ActiveMQQueue;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
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
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.report.api.*;
import se.inera.statistics.service.report.listener.AldersGruppListener;
import se.inera.statistics.service.report.listener.SjukfallPerDiagnosgruppListener;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.Verksamhet;
import se.inera.statistics.service.scheduler.NationellUpdaterJob;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:process-log-qm-test.xml" })
@Transactional
@DirtiesContext
public class RepresentativeIntygIntegrationTest {
    private static final int PERSON_K1950 = 0;
    private static final int PERSON_K1960 = 1;
    private static final int PERSON_M1979 = 2;
    private static final int PERSON_K1990 = 3;
    private static final int PERSON_M1991 = 4;
    private static final int PERSON_M1998 = 5;

    private static final Logger LOG = LoggerFactory.getLogger(RepresentativeIntygIntegrationTest.class);
    public static final String G01 = "G01";
    public static final String L99 = "L99";
    public static final String A00 = "A00";
    public static final int ENVE = 0;
    public static final int TVAVE = 1;

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
    private Overview overview;
    @Autowired
    private CasesPerCounty casesPerCounty;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private NationellUpdaterJob nationellUpdaterJob;
    private String nationell;

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
        persons.add(personNummers.get(5500)); // m 1998
        nationell = Verksamhet.NATIONELL.name();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deliver_document_from_in_queue_to_statistics_repository() throws IOException {
        AldersGruppListener.setMaxCacheSize(1);
        SjukfallPerDiagnosgruppListener.setMaxCacheSize(1);
        UtlatandeBuilder builder = new UtlatandeBuilder("/json/integration/intyg1.json", "Intyg med 1 sjuktal");

        LOG.info("===========START==========");
        LOG.info("3 persons, 1 intyg each, basic test.");

        LOG.info("===========INPUT==========");
        for (TestIntyg intyg : getIntygWithHelsjukAndSingelmanadAndSingelDiagnos(getPerson(PERSON_K1950), getPerson(PERSON_K1960), getPerson(PERSON_M1979))) {
            LOG.info("Intyg: " + intyg);
            simpleSend(builder.build(intyg.personNr, intyg.startDate, intyg.endDate, intyg.vardenhet, intyg.vardgivare, intyg.diagnos, intyg.grads).toString(),
                    "001");
        }

        sleep();

        assertEquals("Verify that all messages have been processed.", 3, consumer.processBatch());

        nationellUpdaterJob.checkLog();

        LOG.info("===========RESULT=========");
        Map<String, TestData> result = printAndGetPersistedData();

        LOG.info("============END===========\n");

        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth1 = (SimpleDualSexResponse<SimpleDualSexDataRow>) result.get("casesPerMonth1").replyObject;
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonthNationell = (SimpleDualSexResponse<SimpleDualSexDataRow>) result.get("casesPerMonthNationell").replyObject;

//        assertEquals(12, casesPerMonth1.getRows().size());
//        assertEquals(12, casesPerMonthNationell.getRows().size());
//
//        for (int i = 0; i < 1; i++) {
//            assertEquals(2, casesPerMonth1.getRows().get(i).getFemale().intValue());
//            assertEquals(1, casesPerMonth1.getRows().get(i).getMale().intValue());
//        }
//        for (int i = 1; i < 12; i++) {
//            assertEquals(0, casesPerMonth1.getRows().get(i).getFemale().intValue());
//            assertEquals(0, casesPerMonth1.getRows().get(i).getMale().intValue());
//        }
    }

    @Test
    @SuppressWarnings("unchecked")
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
            simpleSend(builder.build(intyg.personNr, intyg.startDate, intyg.endDate, intyg.vardenhet, intyg.vardgivare, intyg.diagnos, intyg.grads).toString(),
                    "001");
        }

        sleep();

        assertEquals("Verify that all messages have been processed.", 27, consumer.processBatch());

        nationellUpdaterJob.checkLog();

        LOG.info("===========RESULT=========");
        Map<String, TestData> result = printAndGetPersistedData();

        LOG.info("============END===========\n");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deliver_document_from_in_queue_to_statistics_repository_from_csv_file() throws IOException {
        AldersGruppListener.setMaxCacheSize(1);
        SjukfallPerDiagnosgruppListener.setMaxCacheSize(1);
        UtlatandeBuilder builder1 = new UtlatandeBuilder("/json/integration/intyg1.json", "Intyg med 1 sjuktal");
        UtlatandeBuilder builder2 = new UtlatandeBuilder("/json/integration/intyg2.json", "Intyg med 2 sjuktal");
        UtlatandeBuilder builder3 = new UtlatandeBuilder("/json/integration/intyg3.json", "Intyg med 3 sjuktal");
        UtlatandeBuilder builder4 = new UtlatandeBuilder("/json/integration/intyg4.json", "Intyg med 4 sjuktal");
        UtlatandeBuilder[] builders = {builder1, builder2, builder3, builder4};

        LOG.info("===========START==========");
        LOG.info("testfall.csv");

        LOG.info("===========INPUT==========");
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/testfall.csv")));
        List<String[]> lines = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            lines.add(line.split(";"));
        }
        for (String[] cols : lines) {
            String person = cols[1];
            String diagnos = cols[2];
            List<LocalDate> start = new ArrayList<>();
            List<LocalDate> stop = new ArrayList<>();
            List<String> grad = new ArrayList<>();
            if (!cols[3].equals("")) {
                start.add(new LocalDate(cols[3]));
                stop.add(new LocalDate(cols[4]));
                grad.add(cols[5]);
            }
            if (!cols[6].equals("")) {
                start.add(new LocalDate(cols[6]));
                stop.add(new LocalDate(cols[7]));
                grad.add(cols[8]);
            }
            if (!cols[9].equals("")) {
                start.add(new LocalDate(cols[9]));
                stop.add(new LocalDate(cols[10]));
                grad.add(cols[11]);
            }
            if (!cols[12].equals("")) {
                start.add(new LocalDate(cols[12]));
                stop.add(new LocalDate(cols[13]));
                grad.add(cols[14]);
            }
            String enhet = cols[15];
            String vardgivare = cols[16];
            LOG.info(person + ", " + start + ", " + stop + ", " + enhet + ", " + vardgivare + ", " + diagnos + ", " + grad);
            simpleSend(builders[start.size()-1].build(person, start, stop, enhet, vardgivare, diagnos, grad).toString(), "001");
        }

        sleep();

        assertEquals("Verify that all messages have been processed.", 33, consumer.processBatch());

        nationellUpdaterJob.checkLog();

        LOG.info("===========RESULT=========");
        Map<String, TestData> result = printAndGetPersistedData();
        JsonNode casesPerMonth1 = result.get("casesPerMonth1").jsonNode;


        LOG.info("============END===========\n");
    }

    private Map<String, TestData> printAndGetPersistedData() {
        Map<String, TestData> result = new HashMap<>();
        printAndGetCasesPerMonth(result);
        printAndGetDiagnosisGroups(result);
        printAndGetDiagnosisSubGroups(result);
        printAndGetAgeGroups(result);
        printAndGetDegreeOfSickLeave(result);
        printAndGetSjukfallslangdGrupp(result);
        printAndGetCasesPerCountyNationell(result);

        VerksamhetOverviewResponse verksamhetOverview1 = verksamhetOverview.getOverview(getVardenhet(ENVE), new Range(getStart(0), getStop(3)));
        LOG.info("VO data: " + verksamhetOverview1);
        VerksamhetOverviewResponse verksamhetOverview2 = verksamhetOverview.getOverview(getVardenhet(TVAVE), new Range(getStart(0), getStop(3)));
        LOG.info("VO data: " + verksamhetOverview2);
        OverviewResponse overviewNationell = overview.getOverview(new Range(getStart(0), getStop(3)));
        LOG.info("NO data: " + overviewNationell);

        return result;
    }

    private void printAndGetCasesPerCountyNationell(Map<String, TestData> result) {
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerCountyNationell = casesPerCounty.getStatistics(new Range(getStart(0), getStop(3)));
        LOG.info("CPC: " + casesPerCountyNationell);
        JsonNode casesPerCountyNationellNode = JSONParser.parse(casesPerCountyNationell.toString());
        result.put("casesPerCountyNationell", new TestData(casesPerCountyNationell, casesPerCountyNationellNode));
    }

    private void printAndGetSjukfallslangdGrupp(Map<String, TestData> result) {
        SickLeaveLengthResponse sjukfallslangdGrupp1 = sjukfallslangdGrupp.getHistoricalStatistics(getVardenhet(ENVE), getStart(0), RollingLength.YEAR);
        LOG.info("SLG data: " + sjukfallslangdGrupp1);
        JsonNode sjukfallslangdGrupp1Node = JSONParser.parse(sjukfallslangdGrupp1.toString());
        result.put("sjukfallslangdGrupp1", new TestData(sjukfallslangdGrupp1, sjukfallslangdGrupp1Node));
        SickLeaveLengthResponse sjukfallslangdGrupp2 = sjukfallslangdGrupp.getHistoricalStatistics(getVardenhet(TVAVE), getStart(0), RollingLength.YEAR);
        LOG.info("SLG data: " + sjukfallslangdGrupp2);
        JsonNode sjukfallslangdGrupp2Node = JSONParser.parse(sjukfallslangdGrupp2.toString());
        result.put("sjukfallslangdGrupp2", new TestData(sjukfallslangdGrupp2, sjukfallslangdGrupp2Node));
        SickLeaveLengthResponse sjukfallslangdGruppNationell = sjukfallslangdGrupp.getHistoricalStatistics(nationell, getStart(0), RollingLength.YEAR);
        LOG.info("Nationell SLG data: " + sjukfallslangdGruppNationell);
        JsonNode sjukfallslangdGruppNationellNode = JSONParser.parse(sjukfallslangdGruppNationell.toString());
        result.put("sjukfallslangdGruppNationell", new TestData(sjukfallslangdGruppNationell, sjukfallslangdGruppNationellNode));
        SimpleDualSexResponse<SimpleDualSexDataRow> sjukfallslangdGruppLong1 = sjukfallslangdGrupp.getLongSickLeaves(getVardenhet(ENVE), new Range(getStart(0),
                getStop(3)));
        LOG.info("SLGL data: " + sjukfallslangdGruppLong1);
        JsonNode sjukfallslangdGruppLong1Node = JSONParser.parse(sjukfallslangdGruppLong1.toString());
        result.put("sjukfallslangdGruppLong1", new TestData(sjukfallslangdGruppLong1, sjukfallslangdGruppLong1Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> sjukfallslangdGruppLong2 = sjukfallslangdGrupp.getLongSickLeaves(getVardenhet(TVAVE), new Range(
                getStart(0), getStop(3)));
        LOG.info("SLGL data: " + sjukfallslangdGruppLong2);
        JsonNode sjukfallslangdGruppLong2Node = JSONParser.parse(sjukfallslangdGruppLong2.toString());
        result.put("sjukfallslangdGruppLong2", new TestData(sjukfallslangdGruppLong2, sjukfallslangdGruppLong2Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> sjukfallslangdGruppLongNationell = sjukfallslangdGrupp.getLongSickLeaves(nationell, new Range(getStart(0),
                getStop(3)));
        LOG.info("Nationell SLGL data: " + sjukfallslangdGruppLongNationell);
        JsonNode sjukfallslangdGruppLongNationellNode = JSONParser.parse(sjukfallslangdGruppLongNationell.toString());
        result.put("sjukfallslangdGruppLongNationell", new TestData(sjukfallslangdGruppLongNationell, sjukfallslangdGruppLongNationellNode));
    }

    private void printAndGetDegreeOfSickLeave(Map<String, TestData> result) {
        DegreeOfSickLeaveResponse degreeOfSickLeave1 = degreeOfSickLeave.getStatistics(getVardenhet(ENVE), new Range(getStart(0), getStop(3)));
        LOG.info("DOSL data: " + degreeOfSickLeave1);
        JsonNode degreeOfSickLeave1Node = JSONParser.parse(degreeOfSickLeave1.toString());
        result.put("degreeOfSickLeave1", new TestData(degreeOfSickLeave1, degreeOfSickLeave1Node));
        DegreeOfSickLeaveResponse degreeOfSickLeave2 = degreeOfSickLeave.getStatistics(getVardenhet(TVAVE), new Range(getStart(0), getStop(3)));
        LOG.info("DOSL data: " + degreeOfSickLeave2);
        JsonNode degreeOfSickLeave2Node = JSONParser.parse(degreeOfSickLeave2.toString());
        result.put("degreeOfSickLeave2", new TestData(degreeOfSickLeave2, degreeOfSickLeave2Node));
        DegreeOfSickLeaveResponse degreeOfSickLeaveNationell = degreeOfSickLeave.getStatistics(nationell, new Range(getStart(0), getStop(3)));
        LOG.info("Nationell DOSL data: " + degreeOfSickLeaveNationell);
        JsonNode degreeOfSickLeaveNationellNode = JSONParser.parse(degreeOfSickLeaveNationell.toString());
        result.put("degreeOfSickLeaveNationell", new TestData(degreeOfSickLeaveNationell, degreeOfSickLeaveNationellNode));
    }

    private void printAndGetAgeGroups(Map<String, TestData> result) {
        AgeGroupsResponse ageGroups1 = ageGroups.getHistoricalAgeGroups(getVardenhet(ENVE), getStart(0), RollingLength.YEAR);
        LOG.info("AG data: " + ageGroups1);
        JsonNode ageGroups1Node = JSONParser.parse(ageGroups1.toString());
        result.put("ageGroups1", new TestData(ageGroups1, ageGroups1Node));
        AgeGroupsResponse ageGroups2 = ageGroups.getHistoricalAgeGroups(getVardenhet(TVAVE), getStart(0), RollingLength.YEAR);
        LOG.info("AG data: " + ageGroups2);
        JsonNode ageGroups2Node = JSONParser.parse(ageGroups2.toString());
        result.put("ageGroups2", new TestData(ageGroups2, ageGroups2Node));
        AgeGroupsResponse ageGroupsNationell = ageGroups.getHistoricalAgeGroups(nationell, getStart(0), RollingLength.YEAR);
        LOG.info("Nationell AG data: " + ageGroupsNationell);
        JsonNode ageGroupsNationellNode = JSONParser.parse(ageGroupsNationell.toString());
        result.put("ageGroupsNationell", new TestData(ageGroupsNationell, ageGroupsNationellNode));
    }

    private void printAndGetDiagnosisSubGroups(Map<String, TestData> result) {
        DiagnosisGroupResponse diagnosisSubGroups1 = diagnosisSubGroups.getDiagnosisGroups(getVardenhet(ENVE), new Range(getStart(0), getStop(3)), "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups1);
        JsonNode diagnosisSubGroups1Node = JSONParser.parse(diagnosisSubGroups1.toString());
        result.put("diagnosisSubGroups1", new TestData(diagnosisSubGroups1, diagnosisSubGroups1Node));
        DiagnosisGroupResponse diagnosisSubGroups2 = diagnosisSubGroups.getDiagnosisGroups(getVardenhet(TVAVE), new Range(getStart(0), getStop(3)), "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups2);
        JsonNode diagnosisSubGroups2Node = JSONParser.parse(diagnosisSubGroups2.toString());
        result.put("diagnosisSubGroups2", new TestData(diagnosisSubGroups2, diagnosisSubGroups2Node));
        DiagnosisGroupResponse diagnosisSubGroupsNationell = diagnosisSubGroups.getDiagnosisGroups(nationell, new Range(getStart(0), getStop(3)), "A00-B99");
        LOG.info("Nationell DSG data: " + diagnosisSubGroupsNationell);
        JsonNode diagnosisSubGroupsNationellNode = JSONParser.parse(diagnosisSubGroupsNationell.toString());
        result.put("diagnosisSubGroupsNationell", new TestData(diagnosisSubGroupsNationell, diagnosisSubGroupsNationellNode));
    }

    private void printAndGetDiagnosisGroups(Map<String, TestData> result) {
        DiagnosisGroupResponse diagnosisGroups1 = diagnosisGroups.getDiagnosisGroups(getVardenhet(ENVE), new Range(getStart(0), getStop(3)));
        LOG.info("DG data: " + diagnosisGroups1);
        JsonNode diagnosisGroups1Node = JSONParser.parse(diagnosisGroups1.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups1, diagnosisGroups1Node));
        LOG.info("DG data: " + diagnosisGroups1Node.toString());
        DiagnosisGroupResponse diagnosisGroups2 = diagnosisGroups.getDiagnosisGroups(getVardenhet(TVAVE), new Range(getStart(0), getStop(3)));
        LOG.info("DG jdata: " + diagnosisGroups2);
        JsonNode diagnosisGroups2Node = JSONParser.parse(diagnosisGroups2.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups2, diagnosisGroups2Node));
        DiagnosisGroupResponse diagnosisGroupsNationell = diagnosisGroups.getDiagnosisGroups(nationell, new Range(getStart(0), getStop(3)));
        LOG.info("Nationell DG data:" + diagnosisGroupsNationell);
        JsonNode diagnosisGroupsNationellNode = JSONParser.parse(diagnosisGroupsNationell.toString());
        result.put("diagnosisGroupsNationell", new TestData(diagnosisGroupsNationell, diagnosisGroupsNationellNode));
    }

    private void printAndGetCasesPerMonth(Map<String, TestData> result) {
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth1 = casesPerMonth.getCasesPerMonth(getVardenhet(ENVE), new Range(getStart(0), getStop(3)));
        LOG.info("CPM data: " + casesPerMonth1);
        JsonNode casesPerMonth1Node = JSONParser.parse(casesPerMonth1.toString());
        result.put("casesPerMonth1", new TestData(casesPerMonth1, casesPerMonth1Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth2 = casesPerMonth.getCasesPerMonth(getVardenhet(TVAVE), new Range(getStart(0), getStop(3)));
        LOG.info("CPM data: " + casesPerMonth2);
        JsonNode casesPerMonth2Node = JSONParser.parse(casesPerMonth2.toString());
        result.put("casesPerMonth2", new TestData(casesPerMonth2, casesPerMonth2Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonthNationell = casesPerMonth.getCasesPerMonth(nationell, new Range(getStart(0), getStop(3)));
        LOG.info("Nationell CPM data: " + casesPerMonthNationell);
        JsonNode casesPerMonthNationellNode = JSONParser.parse(casesPerMonthNationell.toString());
        result.put("casesPerMonthNationell", new TestData(casesPerMonthNationell, casesPerMonthNationellNode));
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
        String[] diagnoser = { "A00" };
        return diagnoser[i];
    }

    private LocalDate getStart(int i) {
        LocalDate[] dates = { new LocalDate("2012-02-01"), new LocalDate("2011-01-11") };
        return dates[i];
    }

    private LocalDate getStop(int i) {
        LocalDate[] dates = { new LocalDate("2012-01-22"), new LocalDate("2011-02-21"), new LocalDate("2011-03-11"), new LocalDate("2013-11-17") };
        return dates[i];
    }

    private String getVardenhet(int i) {
        String[] enheter = { "EnVE", "TvaVE" };
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
            return "TestIntyg{" + "personNr='" + personNr + '\'' + ", grads=" + grads + ", startDate=" + startDate + ", endDate=" + endDate + ", vardenhet='"
                    + vardenhet + '\'' + ", vardgivare='" + vardgivare + '\'' + ", diagnos='" + diagnos + '\'' + '}';
        }
    }

    private class TestData {
        private Object replyObject;
        private JsonNode jsonNode;

        private TestData(Object replyObject, JsonNode jsonNode) {
            this.replyObject = replyObject;
            this.jsonNode = jsonNode;
        }
    }
}
// CHECKSTYLE:ON MagicNumber
