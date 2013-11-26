package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.activemq.command.ActiveMQQueue;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.JmsTemplate;
import se.inera.statistics.service.demo.UtlatandeBuilder;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.queue.Receiver;
import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerCounty;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.Verksamhet;
import se.inera.statistics.service.scheduler.NationellUpdaterJob;

import javax.annotation.PostConstruct;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.ConnectionFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueHelper {
    private static final Logger LOG = LoggerFactory.getLogger(QueueHelper.class);
    public static final int PERSON_ID_COL = 1;
    public static final int DIAGNOS_COL = 2;
    public static final int START1_COL = 3;
    public static final int STOP1_COL = 4;
    public static final int GRAD1_COL = 5;
    public static final int START2_COL = 6;
    public static final int STOP2_COL = 7;
    public static final int GRAD2_COL = 8;
    public static final int START3_COL = 9;
    public static final int STOP3_COL = 10;
    public static final int GRAD3_COL = 11;
    public static final int START4_COL = 12;
    public static final int STOP4_COL = 13;
    public static final int GRAD4_COL = 14;
    public static final int ENHET_COL = 15;
    public static final int VARDGIVARE_COL = 16;

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
    private NationellUpdaterJob nationellUpdaterJob;
    @Autowired
    private LogConsumer consumer;


    private JmsTemplate jmsTemplate;
    private String nationell;

    @Autowired
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void init() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
        nationell = Verksamhet.NATIONELL.name();
    }

    public void simpleSend(final String intyg, final String correlationId) {
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

    public void enqueueFromFile(UtlatandeBuilder[] builders, String csvFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(csvFile)));
        List<String[]> lines = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            lines.add(line.split(";"));
        }
        for (String[] cols : lines) {
            String person = cols[PERSON_ID_COL];
            String diagnos = cols[DIAGNOS_COL];
            List<LocalDate> start = new ArrayList<>();
            List<LocalDate> stop = new ArrayList<>();
            List<String> grad = new ArrayList<>();
            if (!cols[START1_COL].equals("")) {
                start.add(new LocalDate(cols[START1_COL]));
                stop.add(new LocalDate(cols[STOP1_COL]));
                grad.add(cols[GRAD1_COL]);
            }
            if (!cols[START2_COL].equals("")) {
                start.add(new LocalDate(cols[START2_COL]));
                stop.add(new LocalDate(cols[STOP2_COL]));
                grad.add(cols[GRAD2_COL]);
            }
            if (!cols[START3_COL].equals("")) {
                start.add(new LocalDate(cols[START3_COL]));
                stop.add(new LocalDate(cols[STOP3_COL]));
                grad.add(cols[GRAD3_COL]);
            }
            if (!cols[START4_COL].equals("")) {
                start.add(new LocalDate(cols[START4_COL]));
                stop.add(new LocalDate(cols[STOP4_COL]));
                grad.add(cols[GRAD4_COL]);
            }
            String enhet = cols[ENHET_COL];
            String vardgivare = cols[VARDGIVARE_COL];
            LOG.info(person + ", " + start + ", " + stop + ", " + enhet + ", " + vardgivare + ", " + diagnos + ", " + grad);
            simpleSend(builders[start.size() - 1].build(person, start, stop, enhet, vardgivare, diagnos, grad).toString(), "001");
        }
    }

    public void enqueue(UtlatandeBuilder builder, String person, String diagnos, List<LocalDate> start, List<LocalDate> stop, List<String> grad, String enhet, String vardgivare, String transId) {
        simpleSend(builder.build(person, start, stop, enhet, vardgivare, diagnos, grad).toString(), transId);
    }

    public Map<String, TestData> printAndGetPersistedData(String vardenhet1, String vardenhet2, Range range) {
        consumer.processBatch();
        nationellUpdaterJob.checkLog();

        Map<String, TestData> result = new HashMap<>();
        printAndGetCasesPerMonth(vardenhet1, vardenhet2, range, result);
        printAndGetDiagnosisGroups(vardenhet1, vardenhet2, range, result);
        printAndGetDiagnosisSubGroups(vardenhet1, vardenhet2, range, result);
        printAndGetAgeGroups(vardenhet1, vardenhet2, range, result);
        printAndGetDegreeOfSickLeave(vardenhet1, vardenhet2, range, result);
        printAndGetSjukfallslangdGrupp(vardenhet1, vardenhet2, range, result);
        printAndGetCasesPerCountyNationell(range, result);

        VerksamhetOverviewResponse verksamhetOverview1 = verksamhetOverview.getOverview(vardenhet1, range);
        LOG.info("VO data: " + verksamhetOverview1);
        VerksamhetOverviewResponse verksamhetOverview2 = verksamhetOverview.getOverview(vardenhet2, range);
        LOG.info("VO data: " + verksamhetOverview2);
        OverviewResponse overviewNationell = overview.getOverview(range);
        LOG.info("NO data: " + overviewNationell);

        return result;
    }

    private void printAndGetCasesPerCountyNationell(Range range, Map<String, TestData> result) {
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerCountyNationell = casesPerCounty.getStatistics(range);
        LOG.info("CPC: " + casesPerCountyNationell);
        JsonNode casesPerCountyNationellNode = JSONParser.parse(casesPerCountyNationell.toString());
        result.put("casesPerCountyNationell", new TestData(casesPerCountyNationell, casesPerCountyNationellNode));
    }

    private void printAndGetSjukfallslangdGrupp(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        SickLeaveLengthResponse sjukfallslangdGrupp1 = sjukfallslangdGrupp.getHistoricalStatistics(vardenhet1, range.getTo(), RollingLength.YEAR);
        LOG.info("SLG data: " + sjukfallslangdGrupp1);
        JsonNode sjukfallslangdGrupp1Node = JSONParser.parse(sjukfallslangdGrupp1.toString());
        result.put("sjukfallslangdGrupp1", new TestData(sjukfallslangdGrupp1, sjukfallslangdGrupp1Node));
        SickLeaveLengthResponse sjukfallslangdGrupp2 = sjukfallslangdGrupp.getHistoricalStatistics(vardenhet2, range.getTo(), RollingLength.YEAR);
        LOG.info("SLG data: " + sjukfallslangdGrupp2);
        JsonNode sjukfallslangdGrupp2Node = JSONParser.parse(sjukfallslangdGrupp2.toString());
        result.put("sjukfallslangdGrupp2", new TestData(sjukfallslangdGrupp2, sjukfallslangdGrupp2Node));
        SickLeaveLengthResponse sjukfallslangdGruppNationell = sjukfallslangdGrupp.getHistoricalStatistics(nationell, range.getTo(), RollingLength.YEAR);
        LOG.info("Nationell SLG data: " + sjukfallslangdGruppNationell);
        JsonNode sjukfallslangdGruppNationellNode = JSONParser.parse(sjukfallslangdGruppNationell.toString());
        result.put("sjukfallslangdGruppNationell", new TestData(sjukfallslangdGruppNationell, sjukfallslangdGruppNationellNode));
        SimpleDualSexResponse<SimpleDualSexDataRow> sjukfallslangdGruppLong1 = sjukfallslangdGrupp.getLongSickLeaves(vardenhet1, range);
        LOG.info("SLGL data: " + sjukfallslangdGruppLong1);
        JsonNode sjukfallslangdGruppLong1Node = JSONParser.parse(sjukfallslangdGruppLong1.toString());
        result.put("sjukfallslangdGruppLong1", new TestData(sjukfallslangdGruppLong1, sjukfallslangdGruppLong1Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> sjukfallslangdGruppLong2 = sjukfallslangdGrupp.getLongSickLeaves(vardenhet2, range);
        LOG.info("SLGL data: " + sjukfallslangdGruppLong2);
        JsonNode sjukfallslangdGruppLong2Node = JSONParser.parse(sjukfallslangdGruppLong2.toString());
        result.put("sjukfallslangdGruppLong2", new TestData(sjukfallslangdGruppLong2, sjukfallslangdGruppLong2Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> sjukfallslangdGruppLongNationell = sjukfallslangdGrupp.getLongSickLeaves(nationell, range);
        LOG.info("Nationell SLGL data: " + sjukfallslangdGruppLongNationell);
        JsonNode sjukfallslangdGruppLongNationellNode = JSONParser.parse(sjukfallslangdGruppLongNationell.toString());
        result.put("sjukfallslangdGruppLongNationell", new TestData(sjukfallslangdGruppLongNationell, sjukfallslangdGruppLongNationellNode));
    }

    private void printAndGetDegreeOfSickLeave(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        DegreeOfSickLeaveResponse degreeOfSickLeave1 = degreeOfSickLeave.getStatistics(vardenhet1, range);
        LOG.info("DOSL data: " + degreeOfSickLeave1);
        JsonNode degreeOfSickLeave1Node = JSONParser.parse(degreeOfSickLeave1.toString());
        result.put("degreeOfSickLeave1", new TestData(degreeOfSickLeave1, degreeOfSickLeave1Node));
        DegreeOfSickLeaveResponse degreeOfSickLeave2 = degreeOfSickLeave.getStatistics(vardenhet2, range);
        LOG.info("DOSL data: " + degreeOfSickLeave2);
        JsonNode degreeOfSickLeave2Node = JSONParser.parse(degreeOfSickLeave2.toString());
        result.put("degreeOfSickLeave2", new TestData(degreeOfSickLeave2, degreeOfSickLeave2Node));
        DegreeOfSickLeaveResponse degreeOfSickLeaveNationell = degreeOfSickLeave.getStatistics(nationell, range);
        LOG.info("Nationell DOSL data: " + degreeOfSickLeaveNationell);
        JsonNode degreeOfSickLeaveNationellNode = JSONParser.parse(degreeOfSickLeaveNationell.toString());
        result.put("degreeOfSickLeaveNationell", new TestData(degreeOfSickLeaveNationell, degreeOfSickLeaveNationellNode));
    }

    private void printAndGetAgeGroups(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        AgeGroupsResponse ageGroups1 = ageGroups.getHistoricalAgeGroups(vardenhet1, range.getFrom(), RollingLength.YEAR);
        LOG.info("AG data: " + ageGroups1);
        JsonNode ageGroups1Node = JSONParser.parse(ageGroups1.toString());
        result.put("ageGroups1", new TestData(ageGroups1, ageGroups1Node));
        AgeGroupsResponse ageGroups2 = ageGroups.getHistoricalAgeGroups(vardenhet2, range.getFrom(), RollingLength.YEAR);
        LOG.info("AG data: " + ageGroups2);
        JsonNode ageGroups2Node = JSONParser.parse(ageGroups2.toString());
        result.put("ageGroups2", new TestData(ageGroups2, ageGroups2Node));
        AgeGroupsResponse ageGroupsNationell = ageGroups.getHistoricalAgeGroups(nationell, range.getFrom(), RollingLength.YEAR);
        LOG.info("Nationell AG data: " + ageGroupsNationell);
        JsonNode ageGroupsNationellNode = JSONParser.parse(ageGroupsNationell.toString());
        result.put("ageGroupsNationell", new TestData(ageGroupsNationell, ageGroupsNationellNode));
    }

    private void printAndGetDiagnosisSubGroups(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        DiagnosisGroupResponse diagnosisSubGroups1 = diagnosisSubGroups.getDiagnosisGroups(vardenhet1, range, "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups1);
        JsonNode diagnosisSubGroups1Node = JSONParser.parse(diagnosisSubGroups1.toString());
        result.put("diagnosisSubGroups1", new TestData(diagnosisSubGroups1, diagnosisSubGroups1Node));
        DiagnosisGroupResponse diagnosisSubGroups2 = diagnosisSubGroups.getDiagnosisGroups(vardenhet2, range, "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups2);
        JsonNode diagnosisSubGroups2Node = JSONParser.parse(diagnosisSubGroups2.toString());
        result.put("diagnosisSubGroups2", new TestData(diagnosisSubGroups2, diagnosisSubGroups2Node));
        DiagnosisGroupResponse diagnosisSubGroupsNationell = diagnosisSubGroups.getDiagnosisGroups(nationell, range, "A00-B99");
        LOG.info("Nationell DSG data: " + diagnosisSubGroupsNationell);
        JsonNode diagnosisSubGroupsNationellNode = JSONParser.parse(diagnosisSubGroupsNationell.toString());
        result.put("diagnosisSubGroupsNationell", new TestData(diagnosisSubGroupsNationell, diagnosisSubGroupsNationellNode));
    }

    private void printAndGetDiagnosisGroups(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        DiagnosisGroupResponse diagnosisGroups1 = diagnosisGroups.getDiagnosisGroups(vardenhet1, range);
        LOG.info("DG data: " + diagnosisGroups1);
        JsonNode diagnosisGroups1Node = JSONParser.parse(diagnosisGroups1.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups1, diagnosisGroups1Node));
        LOG.info("DG data: " + diagnosisGroups1Node.toString());
        DiagnosisGroupResponse diagnosisGroups2 = diagnosisGroups.getDiagnosisGroups(vardenhet2, range);
        LOG.info("DG jdata: " + diagnosisGroups2);
        JsonNode diagnosisGroups2Node = JSONParser.parse(diagnosisGroups2.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups2, diagnosisGroups2Node));
        DiagnosisGroupResponse diagnosisGroupsNationell = diagnosisGroups.getDiagnosisGroups(nationell, range);
        LOG.info("Nationell DG data:" + diagnosisGroupsNationell);
        JsonNode diagnosisGroupsNationellNode = JSONParser.parse(diagnosisGroupsNationell.toString());
        result.put("diagnosisGroupsNationell", new TestData(diagnosisGroupsNationell, diagnosisGroupsNationellNode));
    }

    private void printAndGetCasesPerMonth(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth1 = casesPerMonth.getCasesPerMonth(vardenhet1, range);
        LOG.info("CPM data: " + casesPerMonth1);
        JsonNode casesPerMonth1Node = JSONParser.parse(casesPerMonth1.toString());
        result.put("casesPerMonth1", new TestData(casesPerMonth1, casesPerMonth1Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth2 = casesPerMonth.getCasesPerMonth(vardenhet2, range);
        LOG.info("CPM data: " + casesPerMonth2);
        JsonNode casesPerMonth2Node = JSONParser.parse(casesPerMonth2.toString());
        result.put("casesPerMonth2", new TestData(casesPerMonth2, casesPerMonth2Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonthNationell = casesPerMonth.getCasesPerMonth(nationell, range);
        LOG.info("Nationell CPM data: " + casesPerMonthNationell);
        JsonNode casesPerMonthNationellNode = JSONParser.parse(casesPerMonthNationell.toString());
        result.put("casesPerMonthNationell", new TestData(casesPerMonthNationell, casesPerMonthNationellNode));
    }

}
