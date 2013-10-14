package se.inera.statistics.web.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.OverviewData;

@Service("chartService")
public class ChartDataService {

    private static final int AGE_PERIOD = 12;

    private Overview datasourceOverview;
    private CasesPerMonth datasourceCasesPerMonth;
    private DiagnosisGroups datasourceDiagnosisGroups;
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    private AgeGroups datasourceAgeGroups;
    private DegreeOfSickLeave dataSourceDegreeOfSickLeave;

    public ChartDataService(Overview overviewPersistenceHandler,
                            CasesPerMonth casesPerMonthPersistenceHandler,
                            DiagnosisGroups diagnosisGroupsPersistenceHandler,
                            DiagnosisSubGroups diagnosisSubGroupsPersistenceHandler,
                            AgeGroups ageGroupsPersistenceHandler,
                            DegreeOfSickLeave degreeOfSickLeavePersistenceHandler) {
        datasourceOverview = overviewPersistenceHandler;
        datasourceCasesPerMonth = casesPerMonthPersistenceHandler;
        datasourceDiagnosisGroups = diagnosisGroupsPersistenceHandler;
        datasourceDiagnosisSubGroups = diagnosisSubGroupsPersistenceHandler;
        datasourceAgeGroups = ageGroupsPersistenceHandler;
        dataSourceDegreeOfSickLeave = degreeOfSickLeavePersistenceHandler;
    }

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getNumberOfCasesPerMonth() {
        Range range = new Range();

        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(CasesPerMonth.HSA_NATIONELL, range);

        return new CasesPerMonthConverter().convertCasesPerMonthData(casesPerMonth);
    }

    @GET
    @Path("getDiagnosisGroups")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<DiagnosisGroup> getDiagnosisGroups() {
        return DiagnosisGroupsUtil.getAllDiagnosisGroups();
    }

    @GET
    @Path("getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisGroupStatistics() {
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(DiagnosisSubGroups.HSA_NATIONELL, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@QueryParam("groupId") String groupId) {
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisGroups(DiagnosisSubGroups.HSA_NATIONELL, range, groupId);
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
        Range range = new Range(AGE_PERIOD);
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getAgeGroups(AgeGroups.HSA_NATIONELL, range);
        return new AgeGroupsConverter().convert(ageGroups);
    }

    @GET
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics() {
        DegreeOfSickLeaveResponse degreeOfSickLeaveStatistics = dataSourceDegreeOfSickLeave.getStatistics(DegreeOfSickLeave.HSA_NATIONELL);
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics);
    }
}
