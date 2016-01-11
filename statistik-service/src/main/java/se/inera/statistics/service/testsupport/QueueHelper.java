/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.testsupport;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.NationellData;
import se.inera.statistics.service.warehouse.NationellOverviewData;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WarehouseManager;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.OverviewQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueHelper {
    private static final Logger LOG = LoggerFactory.getLogger(QueueHelper.class);
    public static final int YEAR = 12;
    public static final int QUARTER = 3;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private QueueSender sender;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private WarehouseManager warehouseManager;

    @Autowired
    private OverviewQuery overviewQuery;

    @Autowired
    private DiagnosgruppQuery diagnosgruppQuery;

    @Autowired
    private NationellOverviewData nationellOverview;

    @Autowired
    private NationellData nationell;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private SjukfallQuery sjukfallQuery;

    // CHECKSTYLE:OFF ParameterNumberCheck
    public void enqueue(UtlatandeBuilder builder, String typString, String person, String diagnos, List<LocalDate> start, List<LocalDate> stop, List<String> grad, HsaIdEnhet enhet, HsaIdVardgivare vardgivare, String transId) {
        EventType typ = EventType.valueOf(typString);
        sender.simpleSend(builder.build(person, start, stop, enhet, vardgivare, diagnos, grad).toString(), transId, typ);
    }
    // CHECKSTYLE:ON ParameterNumberCheck

    public Map<String, TestData> printAndGetPersistedData(HsaIdEnhet vardenhet1, HsaIdEnhet vardenhet2, Range range) {
        consumer.processBatch();
        warehouseManager.loadEnhetAndWideLines();
        Map<String, TestData> result = new HashMap<>();
        printAndGetCasesPerMonth(vardenhet1, vardenhet2, range, result);
        printAndGetDiagnosisGroups(vardenhet1, vardenhet2, range, result);
        printAndGetDiagnosisSubGroups(vardenhet1, vardenhet2, range, result);
        printAndGetAgeGroups(vardenhet1, vardenhet2, range, result);
        printAndGetDegreeOfSickLeave(vardenhet1, vardenhet2, range, result);
        printAndGetSjukfallslangdGrupp(vardenhet1, vardenhet2, range, result);
        printAndGetCasesPerCountyNationell(range, result);

        VerksamhetOverviewResponse verksamhetOverview1 = overviewQuery.getOverview(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet1), ReportUtil.getPreviousPeriod(range).getFrom(), range.getMonths());
        LOG.info("VO data: " + verksamhetOverview1);
        VerksamhetOverviewResponse verksamhetOverview2 = overviewQuery.getOverview(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet2), ReportUtil.getPreviousPeriod(range).getFrom(), range.getMonths());
        LOG.info("VO data: " + verksamhetOverview2);
        OverviewResponse overviewNationell = nationellOverview.getOverview(range);
        LOG.info("NO data: " + overviewNationell);

        return result;
    }

    private void printAndGetCasesPerCountyNationell(Range range, Map<String, TestData> result) {
        SimpleKonResponse<SimpleKonDataRow> casesPerCountyNationell = nationell.getSjukfallPerLan(range);
        LOG.info("CPC: " + casesPerCountyNationell);
        JsonNode casesPerCountyNationellNode = JSONParser.parse(casesPerCountyNationell.toString());
        result.put("casesPerCountyNationell", new TestData(casesPerCountyNationell, casesPerCountyNationellNode));
    }

    private void printAndGetSjukfallslangdGrupp(HsaIdEnhet vardenhet1, HsaIdEnhet vardenhet2, Range range, Map<String, TestData> result) {
        SimpleKonResponse<SimpleKonDataRow> sjukfallslangdGrupp1 = SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet1), range.getFrom(), YEAR, 1, sjukfallUtil);
        LOG.info("SLG data: " + sjukfallslangdGrupp1);
        JsonNode sjukfallslangdGrupp1Node = JSONParser.parse(sjukfallslangdGrupp1.toString());
        result.put("sjukfallslangdGrupp1", new TestData(sjukfallslangdGrupp1, sjukfallslangdGrupp1Node));
        SimpleKonResponse<SimpleKonDataRow> sjukfallslangdGrupp2 = SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet2), range.getFrom(), YEAR, 1, sjukfallUtil);
        LOG.info("SLG data: " + sjukfallslangdGrupp2);
        JsonNode sjukfallslangdGrupp2Node = JSONParser.parse(sjukfallslangdGrupp2.toString());
        result.put("sjukfallslangdGrupp2", new TestData(sjukfallslangdGrupp2, sjukfallslangdGrupp2Node));
        SimpleKonResponse<SimpleKonDataRow> sjukfallslangdGruppNationell = nationell.getSjukfallslangd(range.getFrom(), 1, YEAR);
        LOG.info("Nationell SLG data: " + sjukfallslangdGruppNationell);
        JsonNode sjukfallslangdGruppNationellNode = JSONParser.parse(sjukfallslangdGruppNationell.toString());
        result.put("sjukfallslangdGruppNationell", new TestData(sjukfallslangdGruppNationell, sjukfallslangdGruppNationellNode));
        SimpleKonResponse<SimpleKonDataRow> sjukfallslangdGruppLong1 = SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet1), range.getFrom(), QUARTER, 1, sjukfallUtil);
        LOG.info("SLGL data: " + sjukfallslangdGruppLong1);
        JsonNode sjukfallslangdGruppLong1Node = JSONParser.parse(sjukfallslangdGruppLong1.toString());
        result.put("sjukfallslangdGruppLong1", new TestData(sjukfallslangdGruppLong1, sjukfallslangdGruppLong1Node));
        SimpleKonResponse<SimpleKonDataRow> sjukfallslangdGruppLong2 = SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet2), range.getFrom(), QUARTER, 1, sjukfallUtil);
        LOG.info("SLGL data: " + sjukfallslangdGruppLong2);
        JsonNode sjukfallslangdGruppLong2Node = JSONParser.parse(sjukfallslangdGruppLong2.toString());
        result.put("sjukfallslangdGruppLong2", new TestData(sjukfallslangdGruppLong2, sjukfallslangdGruppLong2Node));
        SimpleKonResponse<SimpleKonDataRow> sjukfallslangdGruppLongNationell = nationell.getLangaSjukfall(range.getFrom(), 1, QUARTER);
        LOG.info("Nationell SLGL data: " + sjukfallslangdGruppLongNationell);
        JsonNode sjukfallslangdGruppLongNationellNode = JSONParser.parse(sjukfallslangdGruppLongNationell.toString());
        result.put("sjukfallslangdGruppLongNationell", new TestData(sjukfallslangdGruppLongNationell, sjukfallslangdGruppLongNationellNode));
    }

    private void printAndGetDegreeOfSickLeave(HsaIdEnhet vardenhet1, HsaIdEnhet vardenhet2, Range range, Map<String, TestData> result) {
        KonDataResponse degreeOfSickLeave1 = SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet1), range.getFrom(), 1, YEAR, sjukfallUtil);
        LOG.info("DOSL data: " + degreeOfSickLeave1);
        JsonNode degreeOfSickLeave1Node = JSONParser.parse(degreeOfSickLeave1.toString());
        result.put("degreeOfSickLeave1", new TestData(degreeOfSickLeave1, degreeOfSickLeave1Node));
        KonDataResponse degreeOfSickLeave2 = SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(new HsaIdVardgivare("vg")), sjukfallUtil.createEnhetFilter(vardenhet2), range.getFrom(), 1, YEAR, sjukfallUtil);
        LOG.info("DOSL data: " + degreeOfSickLeave2);
        JsonNode degreeOfSickLeave2Node = JSONParser.parse(degreeOfSickLeave2.toString());
        result.put("degreeOfSickLeave2", new TestData(degreeOfSickLeave2, degreeOfSickLeave2Node));
    }

    private void printAndGetAgeGroups(HsaIdEnhet vardenhet1, HsaIdEnhet vardenhet2, Range range, Map<String, TestData> result) {
        SimpleKonResponse<SimpleKonDataRow> ageGroups1 = AldersgruppQuery.getAldersgrupper(warehouse.get(new HsaIdVardgivare("EnVG")), sjukfallUtil.createEnhetFilter(vardenhet1), range.getFrom(), 1, YEAR, sjukfallUtil);
        LOG.info("AG data: " + ageGroups1);
        JsonNode ageGroups1Node = JSONParser.parse(ageGroups1.toString());
        result.put("ageGroups1", new TestData(ageGroups1, ageGroups1Node));
        SimpleKonResponse<SimpleKonDataRow> ageGroups2 = AldersgruppQuery.getAldersgrupper(warehouse.get(new HsaIdVardgivare("VG2")), sjukfallUtil.createEnhetFilter(vardenhet2), range.getFrom(), 1, YEAR, sjukfallUtil);
        LOG.info("AG data: " + ageGroups2);
        JsonNode ageGroups2Node = JSONParser.parse(ageGroups2.toString());
        result.put("ageGroups2", new TestData(ageGroups2, ageGroups2Node));
    }

    private void printAndGetDiagnosisSubGroups(HsaIdEnhet vardenhet1, HsaIdEnhet vardenhet2, Range range, Map<String, TestData> result) {
        DiagnosgruppResponse diagnosisSubGroups1 = diagnosgruppQuery.getDiagnosavsnitts(warehouse.get(new HsaIdVardgivare("EnVG")), sjukfallUtil.createEnhetFilter(vardenhet1), range.getFrom(), 1, YEAR, "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups1);
        JsonNode diagnosisSubGroups1Node = JSONParser.parse(diagnosisSubGroups1.toString());
        result.put("diagnosisSubGroups1", new TestData(diagnosisSubGroups1, diagnosisSubGroups1Node));
        DiagnosgruppResponse diagnosisSubGroups2 = diagnosgruppQuery.getDiagnosavsnitts(warehouse.get(new HsaIdVardgivare("VG2")), sjukfallUtil.createEnhetFilter(vardenhet2), range.getFrom(), 1, YEAR, "A00-B99");
        LOG.info("DSG data: " + diagnosisSubGroups2);
        JsonNode diagnosisSubGroups2Node = JSONParser.parse(diagnosisSubGroups2.toString());
        result.put("diagnosisSubGroups2", new TestData(diagnosisSubGroups2, diagnosisSubGroups2Node));

        DiagnosgruppResponse nationellDiagnosgrupper = nationell.getDiagnosgrupper(range.getFrom(), range.getMonths(), 1);
        LOG.info("Nationell DSG data: " + nationellDiagnosgrupper);
        JsonNode diagnosisSubGroupsNationellNode = JSONParser.parse(nationellDiagnosgrupper.toString());
        result.put("diagnosisSubGroupsNationell", new TestData(nationellDiagnosgrupper, diagnosisSubGroupsNationellNode));
    }

    private void printAndGetDiagnosisGroups(HsaIdEnhet vardenhet1, HsaIdEnhet vardenhet2, Range range, Map<String, TestData> result) {
        DiagnosgruppResponse diagnosisGroups1 = diagnosgruppQuery.getDiagnosgrupper(warehouse.get(new HsaIdVardgivare("EnVG")), sjukfallUtil.createEnhetFilter(vardenhet1), range.getFrom(), 1, YEAR);
        LOG.info("DG data: " + diagnosisGroups1);
        JsonNode diagnosisGroups1Node = JSONParser.parse(diagnosisGroups1.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups1, diagnosisGroups1Node));
        LOG.info("DG data: " + diagnosisGroups1Node.toString());

        DiagnosgruppResponse diagnosisGroups2 = diagnosgruppQuery.getDiagnosgrupper(warehouse.get(new HsaIdVardgivare("VG2")), sjukfallUtil.createEnhetFilter(vardenhet2), range.getFrom(), 1, YEAR);
        LOG.info("DG jdata: " + diagnosisGroups2);
        JsonNode diagnosisGroups2Node = JSONParser.parse(diagnosisGroups2.toString());
        result.put("diagnosisGroups1", new TestData(diagnosisGroups2, diagnosisGroups2Node));

        DiagnosgruppResponse nationellDiagnosgrupper = nationell.getDiagnosgrupper(range.getFrom(), 1, YEAR);
        LOG.info("Nationell DG data:" + nationellDiagnosgrupper);
        JsonNode diagnosisGroupsNationellNode = JSONParser.parse(nationellDiagnosgrupper.toString());
        result.put("diagnosisGroupsNationell", new TestData(nationellDiagnosgrupper, diagnosisGroupsNationellNode));
    }

    private void printAndGetCasesPerMonth(HsaIdEnhet vardenhet1, HsaIdEnhet vardenhet2, Range range, Map<String, TestData> result) {
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth1 = sjukfallQuery.getSjukfall(warehouse.get(new HsaIdVardgivare("EnVG")), sjukfallUtil.createEnhetFilter(vardenhet1), range.getFrom(), range.getMonths(), 1, false);
        LOG.info("CPM data: " + casesPerMonth1);
        JsonNode casesPerMonth1Node = JSONParser.parse(casesPerMonth1.toString());
        result.put("casesPerMonth1", new TestData(casesPerMonth1, casesPerMonth1Node));
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth2 = sjukfallQuery.getSjukfall(warehouse.get(new HsaIdVardgivare("EnVG")), sjukfallUtil.createEnhetFilter(vardenhet2), range.getFrom(), range.getMonths(), 1, false);
        LOG.info("CPM data: " + casesPerMonth2);
        JsonNode casesPerMonth2Node = JSONParser.parse(casesPerMonth2.toString());
        result.put("casesPerMonth2", new TestData(casesPerMonth2, casesPerMonth2Node));
        SimpleKonResponse<SimpleKonDataRow> casesPerMonthNationell = nationell.getAntalIntyg(range.getFrom(), range.getMonths(), 1);
        LOG.info("Nationell CPM data: " + casesPerMonthNationell);
        JsonNode casesPerMonthNationellNode = JSONParser.parse(casesPerMonthNationell.toString());
        result.put("casesPerMonthNationell", new TestData(casesPerMonthNationell, casesPerMonthNationellNode));
    }

}
