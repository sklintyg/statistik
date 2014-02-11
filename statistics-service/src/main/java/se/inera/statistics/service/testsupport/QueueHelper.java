/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.testsupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.report.api.Aldersgrupp;
import se.inera.statistics.service.report.api.Diagnosgrupp;
import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.api.SjukfallPerLan;
import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.api.Sjukskrivningsgrad;
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

import com.fasterxml.jackson.databind.JsonNode;

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
    private SjukfallPerManad sjukfallPerManad;
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
    private SjukfallPerLan sjukfallPerLan;
    @Autowired
    private NationellUpdaterJob nationellUpdaterJob;
    @Autowired
    private LogConsumer consumer;

    @Autowired
    private QueueSender sender;

    private String nationell;

    @PostConstruct
    public void init() {
        nationell = Verksamhet.NATIONELL.name();
    }

    // CHECKSTYLE:OFF ParameterNumberCheck
    public void enqueue(UtlatandeBuilder builder, String typString, String person, String diagnos, List<LocalDate> start, List<LocalDate> stop, List<String> grad, String enhet, String vardgivare, String transId) {
        EventType typ = EventType.valueOf(typString);
        sender.simpleSend(builder.build(person, start, stop, enhet, vardgivare, diagnos, grad).toString(), transId, typ);
    }
    // CHECKSTYLE:ON ParameterNumberCheck

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
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerCountyNationell = sjukfallPerLan.getStatistics(range);
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
        DegreeOfSickLeaveResponse degreeOfSickLeave1 = sjukskrivningsgrad.getStatistics(vardenhet1, range);
        LOG.info("DOSL data: " + degreeOfSickLeave1);
        JsonNode degreeOfSickLeave1Node = JSONParser.parse(degreeOfSickLeave1.toString());
        result.put("degreeOfSickLeave1", new TestData(degreeOfSickLeave1, degreeOfSickLeave1Node));
        DegreeOfSickLeaveResponse degreeOfSickLeave2 = sjukskrivningsgrad.getStatistics(vardenhet2, range);
        LOG.info("DOSL data: " + degreeOfSickLeave2);
        JsonNode degreeOfSickLeave2Node = JSONParser.parse(degreeOfSickLeave2.toString());
        result.put("degreeOfSickLeave2", new TestData(degreeOfSickLeave2, degreeOfSickLeave2Node));
        DegreeOfSickLeaveResponse degreeOfSickLeaveNationell = sjukskrivningsgrad.getStatistics(nationell, range);
        LOG.info("Nationell DOSL data: " + degreeOfSickLeaveNationell);
        JsonNode degreeOfSickLeaveNationellNode = JSONParser.parse(degreeOfSickLeaveNationell.toString());
        result.put("degreeOfSickLeaveNationell", new TestData(degreeOfSickLeaveNationell, degreeOfSickLeaveNationellNode));
    }

    private void printAndGetAgeGroups(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        AgeGroupsResponse ageGroups1 = aldersgrupp.getHistoricalAgeGroups(vardenhet1, range.getTo(), RollingLength.YEAR);
        LOG.info("AG data: " + ageGroups1);
        JsonNode ageGroups1Node = JSONParser.parse(ageGroups1.toString());
        result.put("ageGroups1", new TestData(ageGroups1, ageGroups1Node));
        AgeGroupsResponse ageGroups2 = aldersgrupp.getHistoricalAgeGroups(vardenhet2, range.getTo(), RollingLength.YEAR);
        LOG.info("AG data: " + ageGroups2);
        JsonNode ageGroups2Node = JSONParser.parse(ageGroups2.toString());
        result.put("ageGroups2", new TestData(ageGroups2, ageGroups2Node));
        AgeGroupsResponse ageGroupsNationell = aldersgrupp.getHistoricalAgeGroups(nationell, range.getTo(), RollingLength.YEAR);
        LOG.info("Nationell AG data: " + ageGroupsNationell);
        JsonNode ageGroupsNationellNode = JSONParser.parse(ageGroupsNationell.toString());
        result.put("ageGroupsNationell", new TestData(ageGroupsNationell, ageGroupsNationellNode));
    }

    private void printAndGetDiagnosisSubGroups(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        DiagnosisGroupResponse diagnosisSubGroups1 = diagnoskapitel.getDiagnosisGroups(vardenhet1, range, "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups1);
        JsonNode diagnosisSubGroups1Node = JSONParser.parse(diagnosisSubGroups1.toString());
        result.put("diagnosisSubGroups1", new TestData(diagnosisSubGroups1, diagnosisSubGroups1Node));
        DiagnosisGroupResponse diagnosisSubGroups2 = diagnoskapitel.getDiagnosisGroups(vardenhet2, range, "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups2);
        JsonNode diagnosisSubGroups2Node = JSONParser.parse(diagnosisSubGroups2.toString());
        result.put("diagnosisSubGroups2", new TestData(diagnosisSubGroups2, diagnosisSubGroups2Node));
        DiagnosisGroupResponse diagnosisSubGroupsNationell = diagnoskapitel.getDiagnosisGroups(nationell, range, "A00-B99");
        LOG.info("Nationell DSG data: " + diagnosisSubGroupsNationell);
        JsonNode diagnosisSubGroupsNationellNode = JSONParser.parse(diagnosisSubGroupsNationell.toString());
        result.put("diagnosisSubGroupsNationell", new TestData(diagnosisSubGroupsNationell, diagnosisSubGroupsNationellNode));
    }

    private void printAndGetDiagnosisGroups(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        DiagnosisGroupResponse diagnosisGroups1 = diagnosgrupp.getDiagnosisGroups(vardenhet1, range);
        LOG.info("DG data: " + diagnosisGroups1);
        JsonNode diagnosisGroups1Node = JSONParser.parse(diagnosisGroups1.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups1, diagnosisGroups1Node));
        LOG.info("DG data: " + diagnosisGroups1Node.toString());
        DiagnosisGroupResponse diagnosisGroups2 = diagnosgrupp.getDiagnosisGroups(vardenhet2, range);
        LOG.info("DG jdata: " + diagnosisGroups2);
        JsonNode diagnosisGroups2Node = JSONParser.parse(diagnosisGroups2.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups2, diagnosisGroups2Node));
        DiagnosisGroupResponse diagnosisGroupsNationell = diagnosgrupp.getDiagnosisGroups(nationell, range);
        LOG.info("Nationell DG data:" + diagnosisGroupsNationell);
        JsonNode diagnosisGroupsNationellNode = JSONParser.parse(diagnosisGroupsNationell.toString());
        result.put("diagnosisGroupsNationell", new TestData(diagnosisGroupsNationell, diagnosisGroupsNationellNode));
    }

    private void printAndGetCasesPerMonth(String vardenhet1, String vardenhet2, Range range, Map<String, TestData> result) {
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth1 = sjukfallPerManad.getCasesPerMonth(vardenhet1, range);
        LOG.info("CPM data: " + casesPerMonth1);
        JsonNode casesPerMonth1Node = JSONParser.parse(casesPerMonth1.toString());
        result.put("casesPerMonth1", new TestData(casesPerMonth1, casesPerMonth1Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth2 = sjukfallPerManad.getCasesPerMonth(vardenhet2, range);
        LOG.info("CPM data: " + casesPerMonth2);
        JsonNode casesPerMonth2Node = JSONParser.parse(casesPerMonth2.toString());
        result.put("casesPerMonth2", new TestData(casesPerMonth2, casesPerMonth2Node));
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonthNationell = sjukfallPerManad.getCasesPerMonth(nationell, range);
        LOG.info("Nationell CPM data: " + casesPerMonthNationell);
        JsonNode casesPerMonthNationellNode = JSONParser.parse(casesPerMonthNationell.toString());
        result.put("casesPerMonthNationell", new TestData(casesPerMonthNationell, casesPerMonthNationellNode));
    }

}
