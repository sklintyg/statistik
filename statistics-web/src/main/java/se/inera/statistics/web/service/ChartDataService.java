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

package se.inera.statistics.web.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.Aldersgrupp;
import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.api.SjukfallPerLan;
import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.api.Sjukskrivningsgrad;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.OverviewData;

@Service("chartService")
public class ChartDataService {

    private static final String NATIONELL = Verksamhet.NATIONELL.toString();

    private static final Logger LOG = LoggerFactory.getLogger(ChartDataService.class);

    @Autowired
    private Overview datasourceOverview;
    @Autowired
    private SjukfallPerManad datasourceSjukfallPerManad;
    @Autowired
    private se.inera.statistics.service.report.api.Diagnosgrupp datasourceDiagnosgrupp;
    @Autowired
    private Diagnoskapitel datasourceDiagnoskapitel;
    @Autowired
    private Aldersgrupp datasourceAldersgrupp;
    @Autowired
    private Sjukskrivningsgrad datasourceSjukskrivningsgrad;
    @Autowired
    private SjukfallslangdGrupp datasourceSickLeaveLength;
    @Autowired
    private SjukfallPerLan datasourceSjukfallPerLan;

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public SimpleDetailsData getNumberOfCasesPerMonth() {
        LOG.info("Calling getNumberOfCasesPerMonth for national");
        final Range range = new Range(18);
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceSjukfallPerManad.getCasesPerMonth(NATIONELL, range);
        return new SimpleDualSexConverter().convert(casesPerMonth, range);
    }

    @GET
    @Path("getNumberOfCasesPerMonth/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getNumberOfCasesPerMonthAsCsv() {
        LOG.info("Calling getNumberOfCasesPerMonthAsCsv for national");
        final TableData tableData = getNumberOfCasesPerMonth().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("getDiagnosisGroups")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<Diagnosgrupp> getDiagnosisGroups() {
        LOG.info("Calling getDiagnosgrupps");
        return DiagnosisGroupsUtil.getAllDiagnosisGroups();
    }

    @GET
    @Path("getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisGroupStatistics() {
        LOG.info("Calling getDiagnosisGroupStatistics for national");
        final Range range = new Range(18);
        DiagnosgruppResponse diagnosisGroups = datasourceDiagnosgrupp.getDiagnosisGroups(NATIONELL, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups, range);
    }

    @GET
    @Path("getDiagnosisGroupStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getDiagnosisGroupStatisticsAsCsv() {
        LOG.info("Calling getDiagnosisGroupStatisticsAsCsv for national");
        final TableData tableData = getDiagnosisGroupStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("getDiagnosisSubGroupStatistics/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosisSubGroupStatistics for national with groupId: " + groupId);
        final Range range = new Range(18);
        DiagnosgruppResponse diagnosisGroups = datasourceDiagnoskapitel.getDiagnosisGroups(NATIONELL, range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups, range);
    }

    @GET
    @Path("getDiagnosisSubGroupStatistics/{groupId}/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getDiagnosisSubGroupStatisticsAsCsv(@PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosisSubGroupStatisticsAsCsv for national");
        final TableData tableData = getDiagnosisSubGroupStatistics(groupId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        Range range = Range.quarter();
        OverviewResponse response = datasourceOverview.getOverview(range);
        return new OverviewConverter().convert(response, range);
    }

    @GET
    @Path("getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public AgeGroupsData getAgeGroupsStatistics() {
        LOG.info("Calling getAgeGroupsStatistics for national");
        final RollingLength quarter = RollingLength.QUARTER;
        SimpleDualSexResponse<SimpleDualSexDataRow> ageGroups = datasourceAldersgrupp.getHistoricalAgeGroups(NATIONELL, previousMonth(), quarter);
        return new AgeGroupsConverter().convert(ageGroups, new Range(quarter.getPeriods()));
    }

    @GET
    @Path("getAgeGroupsStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getAgeGroupsStatisticsAsCsv() {
        LOG.info("Calling getAgeGroupsStatisticsAsCsv for national");
        final TableData tableData = getAgeGroupsStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics() {
        LOG.info("Calling getDegreeOfSickLeaveStatistics for national");
        final Range range = new Range(18);
        SjukskrivningsgradResponse degreeOfSickLeaveStatistics = datasourceSjukskrivningsgrad.getStatistics(NATIONELL, range);
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, range);
    }

    @GET
    @Path("getDegreeOfSickLeaveStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getDegreeOfSickLeaveStatisticsAsCsv() {
        LOG.info("Calling getDegreeOfSickLeaveStatisticsAsCsv for national");
        final TableData tableData = getDegreeOfSickLeaveStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    public SickLeaveLengthData getSickLeaveLengthData() {
        LOG.info("Calling getSickLeaveLengthData for national");
        final RollingLength period = RollingLength.YEAR;
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getHistoricalStatistics(NATIONELL, previousMonth(), period);
        return new SickLeaveLengthConverter().convert(sickLeaveLength, new Range(period.getPeriods()));
    }

    @GET
    @Path("getSickLeaveLengthData/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getSickLeaveLengthDataAsCsv() {
        LOG.info("Calling getSickLeaveLengthDataAsCsv for national");
        final TableData tableData = getSickLeaveLengthData().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("getCountyStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public CasesPerCountyData getCountyStatistics() {
        Range range1 = Range.quarter();
        Range range2 = ReportUtil.getPreviousPeriod(range1);

        SimpleDualSexResponse<SimpleDualSexDataRow> countyStatRange1 = datasourceSjukfallPerLan.getStatistics(range1);
        SimpleDualSexResponse<SimpleDualSexDataRow> countyStatRange2 = datasourceSjukfallPerLan.getStatistics(range2);
        return new CasesPerCountyConverter(countyStatRange1, countyStatRange2, range1, range2).convert();
    }

    @GET
    @Path("getCountyStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getCountyStatisticsAsCsv() {
        LOG.info("Calling getCountyStatisticsAsCsv for national");
        final TableData tableData = getCountyStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("getSjukfallPerSexStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public SimpleDetailsData getSjukfallPerSexStatistics() {
        LOG.info("Calling getSjukfallPerSexStatistics for national");
        final Range range = new Range(12);
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceSjukfallPerLan.getStatistics(range);
        return new SjukfallPerSexConverter().convert(casesPerMonth, range);
    }

    @GET
    @Path("getSjukfallPerSexStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response getSjukfallPerSexStatisticsAsCsv() {
        LOG.info("Calling getSjukfallPerSexStatisticsAsCsv for national");
        final TableData tableData = getSjukfallPerSexStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    private LocalDate previousMonth() {
        return new LocalDate().withDayOfMonth(1).minusMonths(1);
    }

}
