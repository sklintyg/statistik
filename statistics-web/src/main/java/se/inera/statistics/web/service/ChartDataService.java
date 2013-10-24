package se.inera.statistics.web.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import se.inera.statistics.web.model.overview.OverviewData;

@Service("chartService")
public class ChartDataService {

    private static final String NATIONELL = Verksamhet.NATIONELL.toString();

    private static final Logger LOG = LoggerFactory.getLogger(ChartDataService.class);

    private Overview datasourceOverview;
    private CasesPerMonth datasourceCasesPerMonth;
    private DiagnosisGroups datasourceDiagnosisGroups;
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    private AgeGroups datasourceAgeGroups;
    private DegreeOfSickLeave datasourceDegreeOfSickLeave;
    private SjukfallslangdGrupp datasourceSickLeaveLength;
    private CasesPerCounty datasourceCasesPerCounty;

    public ChartDataService(Overview overviewPersistenceHandler,
                            CasesPerMonth casesPerMonthPersistenceHandler,
                            DiagnosisGroups diagnosisGroupsPersistenceHandler,
                            DiagnosisSubGroups diagnosisSubGroupsPersistenceHandler,
                            AgeGroups ageGroupsPersistenceHandler,
                            DegreeOfSickLeave degreeOfSickLeavePersistenceHandler,
                            SjukfallslangdGrupp sickLeaveLengthPersistenceHandler,
                            CasesPerCounty casesPerCountyHandler) {
        datasourceOverview = overviewPersistenceHandler;
        datasourceCasesPerMonth = casesPerMonthPersistenceHandler;
        datasourceDiagnosisGroups = diagnosisGroupsPersistenceHandler;
        datasourceDiagnosisSubGroups = diagnosisSubGroupsPersistenceHandler;
        datasourceAgeGroups = ageGroupsPersistenceHandler;
        datasourceDegreeOfSickLeave = degreeOfSickLeavePersistenceHandler;
        datasourceSickLeaveLength = sickLeaveLengthPersistenceHandler;
        datasourceCasesPerCounty = casesPerCountyHandler;
    }

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public SimpleDetailsData getNumberOfCasesPerMonth() {
        LOG.info("Calling getNumberOfCasesPerMonth for national");
        Range range = new Range();
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(NATIONELL, range);
        return new SimpleDualSexConverter().convert(casesPerMonth);
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
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(NATIONELL, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("getDiagnosisSubGroupStatistics/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosisSubGroupStatistics for national with groupId: " + groupId);
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisGroups(NATIONELL, range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        Range range = Range.quarter();
        OverviewResponse response = datasourceOverview.getOverview(range);
        return new OverviewConverter().convert(response);
    }

    @GET
    @Path("getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public AgeGroupsData getAgeGroupsStatistics() {
        LOG.info("Calling getAgeGroupsStatistics for national");
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getHistoricalAgeGroups(NATIONELL, previousMonth(), RollingLength.QUARTER);
        return new AgeGroupsConverter().convert(ageGroups);
    }

    @GET
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics() {
        LOG.info("Calling getDegreeOfSickLeaveStatistics for national");
        DegreeOfSickLeaveResponse degreeOfSickLeaveStatistics = datasourceDegreeOfSickLeave.getStatistics(NATIONELL, new Range());
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics);
    }

    @GET
    @Path("getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    public SickLeaveLengthData getSickLeaveLengthData() {
        LOG.info("Calling getSickLeaveLengthData for national");
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getHistoricalStatistics(NATIONELL, previousMonth(), RollingLength.YEAR);
        return new SickLeaveLengthConverter().convert(sickLeaveLength);
    }

    @GET
    @Path("getCountyStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public CasesPerCountyData getCountyStatistics() {
        Range range1 = Range.quarter();
        Range range2 = ReportUtil.getPreviousPeriod(range1);

        SimpleDualSexResponse<SimpleDualSexDataRow> countyStatRange1 = datasourceCasesPerCounty.getStatistics(NATIONELL, range1);
        SimpleDualSexResponse<SimpleDualSexDataRow> countyStatRange2 = datasourceCasesPerCounty.getStatistics(NATIONELL, range2);
        return new CasesPerCountyConverter(countyStatRange1, countyStatRange2, range1, range2).convert();
    }

    @GET
    @Path("getSjukfallPerSexStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public SimpleDetailsData getSjukfallPerSexStatistics() {
        LOG.info("Calling getSjukfallPerSexStatistics for national");
        Range range = new Range(RollingLength.YEAR.getPeriods());
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceCasesPerCounty.getStatistics(NATIONELL, range);
        return new SjukfallPerSexConverter().convert(casesPerMonth);
    }

    private LocalDate previousMonth() {
        return new LocalDate().withDayOfMonth(1).minusMonths(1);
    }

}

