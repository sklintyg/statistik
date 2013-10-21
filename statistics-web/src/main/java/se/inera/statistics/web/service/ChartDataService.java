package se.inera.statistics.web.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import se.inera.statistics.service.report.api.SickLeaveLength;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.CasesPerCountyResponse;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.Verksamhet;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.CasesPerMonthData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.overview.OverviewData;

@Service("chartService")
public class ChartDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ChartDataService.class);

    private Overview datasourceOverview;
    private CasesPerMonth datasourceCasesPerMonth;
    private DiagnosisGroups datasourceDiagnosisGroups;
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    private AgeGroups datasourceAgeGroups;
    private DegreeOfSickLeave datasourceDegreeOfSickLeave;
    private SickLeaveLength datasourceSickLeaveLength;
    private CasesPerCounty datasourceCasesPerCounty;

    public ChartDataService(Overview overviewPersistenceHandler,
                            CasesPerMonth casesPerMonthPersistenceHandler,
                            DiagnosisGroups diagnosisGroupsPersistenceHandler,
                            DiagnosisSubGroups diagnosisSubGroupsPersistenceHandler,
                            AgeGroups ageGroupsPersistenceHandler,
                            DegreeOfSickLeave degreeOfSickLeavePersistenceHandler,
                            SickLeaveLength sickLeaveLengthPersistenceHandler,
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
    public CasesPerMonthData getNumberOfCasesPerMonth() {
        LOG.info("Calling getNumberOfCasesPerMonth for national");
        Range range = new Range();
        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(Verksamhet.NATIONELL.toString(), range);
        return new CasesPerMonthConverter().convert(casesPerMonth);
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
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(Verksamhet.NATIONELL.toString(), range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@QueryParam("groupId") String groupId) {
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisGroups(Verksamhet.NATIONELL.toString(), range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        OverviewResponse response = datasourceOverview.getOverview();
        return new OverviewConverter().convert(response);
    }

    @GET
    @Path("getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public AgeGroupsData getAgeGroupsStatistics() {
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getAgeGroups(Verksamhet.NATIONELL.toString(), new LocalDate().minusMonths(1));
        return new AgeGroupsConverter().convert(ageGroups);
    }

    @GET
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics() {
        DegreeOfSickLeaveResponse degreeOfSickLeaveStatistics = datasourceDegreeOfSickLeave.getStatistics(DegreeOfSickLeave.HSA_NATIONELL);
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics);
    }

    @GET
    @Path("getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    public SickLeaveLengthData getSickLeaveLengthData() {
        final int numberOfMonthsToRequest = 12;
        Range range = new Range(numberOfMonthsToRequest);
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getStatistics(SickLeaveLength.HSA_NATIONELL, range);
        return new SickLeaveLengthConverter().convert(sickLeaveLength);
    }

    @GET
    @Path("getCountyStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public CasesPerCountyData getCountyStatistics() {
        final int numberOfMonthsInRanges = 3;
        LocalDate range1To = new LocalDate().withDayOfMonth(1).minusMonths(1);
        LocalDate range1From = range1To.minusMonths(numberOfMonthsInRanges);
        final Range range1 = new Range(range1From, range1To);

        LocalDate range2To = range1From.minusMonths(1);
        LocalDate range2From = range2To.minusMonths(numberOfMonthsInRanges);
        final Range range2 = new Range(range2From, range2To);

        CasesPerCountyResponse countyStatRange1 = datasourceCasesPerCounty.getStatistics(SickLeaveLength.HSA_NATIONELL, range1);
        CasesPerCountyResponse countyStatRange2 = datasourceCasesPerCounty.getStatistics(SickLeaveLength.HSA_NATIONELL, range2);
        return new CasesPerCountyConverter(countyStatRange1, countyStatRange2, range1, range2).convert();
    }

}

