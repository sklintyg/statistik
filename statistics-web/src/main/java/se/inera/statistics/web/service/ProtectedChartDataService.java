package se.inera.statistics.web.service;

import org.springframework.security.access.prepost.PreAuthorize;
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
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.Business;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.OverviewData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Service("protectedChartService")
@Path("/business")
public class ProtectedChartDataService {

    private static final int AGE_PERIOD = 12;

    private Overview datasourceOverview;
    private CasesPerMonth datasourceCasesPerMonth;
    private DiagnosisGroups datasourceDiagnosisGroups;
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    private AgeGroups datasourceAgeGroups;

    public ProtectedChartDataService() {

    }

    public ProtectedChartDataService(Overview overviewPersistenceHandler,
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
        Range range = new Range();
        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(range);
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
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }


    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@QueryParam("groupId") String groupId) {
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisGroups(range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("getOverview/{verksamhetId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    public OverviewData getOverviewData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        OverviewResponse response = datasourceOverview.getOverview(verksamhetId);
        return new OverviewConverter().convert(response);
    }

    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessToAtLeastOne(#request)")
    public OverviewData getOverviewData(@Context HttpServletRequest request) {
        String verksamhetId = ((List<Business>) getSession(request).getAttribute("verksamhetId")).get(0).getId();
        OverviewResponse response = datasourceOverview.getOverview(verksamhetId);
        return new OverviewConverter().convert(response);
    }

    private HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }

    @GET
    @Path("getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public AgeGroupsData getAgeGroupsStatistics() {
        Range range = new Range(AGE_PERIOD);
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getAgeGroups(range);
        return new AgeGroupsConverter().convert(ageGroups);
    }

    public boolean hasAccessTo(HttpServletRequest request, String verksamhetId) {
        if (request == null) {
            return false;
        }
        List<Business> verksamhets = (List<Business>) getSession(request).getAttribute("verksamhets");
        if (verksamhetId != null && verksamhets != null) {
            for (Business verksamhet : verksamhets) {
                if (verksamhetId.equals(verksamhet.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAccessToAtLeastOne(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        List<Business> verksamhets = (List<Business>) getSession(request).getAttribute("verksamhets");
        if (verksamhets == null || verksamhets.isEmpty()) {
            return true;
        }
        return false;
    }
}
