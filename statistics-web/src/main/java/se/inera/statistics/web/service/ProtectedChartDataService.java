package se.inera.statistics.web.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

@Service("protectedChartService")
@Path("/verksamhet")
public class ProtectedChartDataService {

    private static final int AGE_PERIOD = 12;

    private VerksamhetOverview datasourceOverview;
    private CasesPerMonth datasourceCasesPerMonth;
    private DiagnosisGroups datasourceDiagnosisGroups;
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    private AgeGroups datasourceAgeGroups;

    public ProtectedChartDataService() {

    }

    public ProtectedChartDataService(VerksamhetOverview overviewPersistenceHandler,
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
    @Path("getNumberOfCasesPerMonth/{verksamhetId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getNumberOfCasesPerMonth(@PathParam("verksamhetId") String verksamhetId) {
        Range range = new Range();
        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(Verksamhet.decodeId(verksamhetId), range);
        return new CasesPerMonthConverter().convertCasesPerMonthData(casesPerMonth);
    }

    @GET
    @Path("getDiagnosisGroups")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<DiagnosisGroup> getDiagnosisGroups() {
        return DiagnosisGroupsUtil.getAllDiagnosisGroups();
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisGroupStatistics(@PathParam("verksamhetId") String verksamhetId) {
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(verksamhetId, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }


    @GET
    @Path("{verksamhetId}/getDiagnosisSubGroupStatistics/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@PathParam("verksamhetId") String verksamhetId, @PathParam("groupId") String groupId) {
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisGroups(Verksamhet.decodeId(verksamhetId), range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("{verksamhetId}/getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    public VerksamhetOverviewData getOverviewData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        Range range = new Range();
        VerksamhetOverviewResponse response = datasourceOverview.getOverview(Verksamhet.decodeId(verksamhetId), range);
        return new VerksamhetOverviewConverter().convert(response);
    }

    @GET
    @Path("{verksamhetId}/getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public AgeGroupsData getAgeGroupsStatistics(@PathParam("verksamhetId") String verksamhetId) {
        Range range = new Range(AGE_PERIOD);
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getAgeGroups("hsaid", range);
        return new AgeGroupsConverter().convert(ageGroups);
    }

    public boolean hasAccessTo(HttpServletRequest request, String verksamhetId) {
        if (request == null) {
            return false;
        }
        List<Verksamhet> verksamhets = getVerksamhets(request);
        if (verksamhetId != null && verksamhets != null) {
            for (Verksamhet verksamhet : verksamhets) {
                if (verksamhetId.equals(verksamhet.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The default verksamhet to be used is determined later.
     * This method only ensures that user has access to at least one verksamhet from which to select a default.
     * (This is enough since the exact verksamhet to use is picked from a list of authorized verksamhets.)
     *
     * @param request http request
     * @return
     */
    public boolean hasAccessToAtLeastOne(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        List<Verksamhet> verksamhets = getVerksamhets(request);
        if (verksamhets == null || verksamhets.isEmpty()) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private List<Verksamhet> getVerksamhets(HttpServletRequest request) {
        return (List<Verksamhet>) request.getSession().getAttribute("verksamhets");
    }
}
