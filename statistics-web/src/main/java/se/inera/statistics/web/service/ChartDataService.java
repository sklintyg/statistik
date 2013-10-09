package se.inera.statistics.web.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.OverviewData;

@Service("chartService")
public class ChartDataService {

    private static final int INCUSIVE_PERIOD = 18;

    private Overview datasourceOverview;
    private CasesPerMonth datasourceCasesPerMonth;
    private DiagnosisGroups datasourceDiagnosisGroups;
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    private AgeGroups datasourceAgeGroups;

    public ChartDataService(Overview overviewPersistenceHandler,
                            CasesPerMonth casesPerMonthPersistenceHandler,
                            DiagnosisGroups diagnosisGroupsPersistenceHandler,
                            DiagnosisSubGroups diagnosisSubGroupsPersistenceHandler,
                            AgeGroups ageGroupsPersistenceHandler) {
        datasourceOverview = overviewPersistenceHandler;
        datasourceCasesPerMonth = casesPerMonthPersistenceHandler;
        datasourceDiagnosisGroups = diagnosisGroupsPersistenceHandler;
        datasourceDiagnosisSubGroups = diagnosisSubGroupsPersistenceHandler;
        datasourceAgeGroups = ageGroupsPersistenceHandler;

    }

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getNumberOfCasesPerMonth() {
        LocalDate lastMonth = new LocalDate().withDayOfMonth(1).minusMonths(1);

        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(lastMonth.minusMonths(INCUSIVE_PERIOD - 1), lastMonth);

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
    public DiagnosisGroupsData getDiagnosisGroupStatistics() {
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups();
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }


    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DiagnosisGroupsData getDiagnosisSubGroupStatistics(@QueryParam("groupId") String groupId) {
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisSubGroups(groupId);
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
        final int numberOfMonthsToShow = 12;
        LocalDate lastMonth = new LocalDate().withDayOfMonth(1).minusMonths(1);
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getAgeGroups(lastMonth.minusMonths(numberOfMonthsToShow), lastMonth);
        return new AgeGroupsConverter().convert(ageGroups);
    }

}
