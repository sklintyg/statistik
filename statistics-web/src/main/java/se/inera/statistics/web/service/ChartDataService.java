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

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerCounty;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
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
    private CasesPerMonth datasourceCasesPerMonth;
    @Autowired
    private DiagnosisGroups datasourceDiagnosisGroups;
    @Autowired
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    @Autowired
    private AgeGroups datasourceAgeGroups;
    @Autowired
    private DegreeOfSickLeave datasourceDegreeOfSickLeave;
    @Autowired
    private SjukfallslangdGrupp datasourceSickLeaveLength;
    @Autowired
    private CasesPerCounty datasourceCasesPerCounty;

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public SimpleDetailsData getNumberOfCasesPerMonth() {
        LOG.info("Calling getNumberOfCasesPerMonth for national");
        final Range range = new Range(18);
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(NATIONELL, range);
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
    public List<DiagnosisGroup> getDiagnosisGroups() {
        LOG.info("Calling getDiagnosisGroups");
        return DiagnosisGroupsUtil.getAllDiagnosisGroups();
    }

    @GET
    @Path("getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisGroupStatistics() {
        LOG.info("Calling getDiagnosisGroupStatistics for national");
        final Range range = new Range(18);
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(NATIONELL, range);
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
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisGroups(NATIONELL, range, groupId);
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
        final RollingLength period = RollingLength.YEAR;
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getHistoricalAgeGroups(NATIONELL, previousMonth(), period);
        return new AgeGroupsConverter().convert(ageGroups, new Range(period.getPeriods()));
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
        DegreeOfSickLeaveResponse degreeOfSickLeaveStatistics = datasourceDegreeOfSickLeave.getStatistics(NATIONELL, range);
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

        SimpleDualSexResponse<SimpleDualSexDataRow> countyStatRange1 = datasourceCasesPerCounty.getStatistics(range1);
        SimpleDualSexResponse<SimpleDualSexDataRow> countyStatRange2 = datasourceCasesPerCounty.getStatistics(range2);
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
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceCasesPerCounty.getStatistics(range);
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
