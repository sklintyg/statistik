package se.inera.statistics.web.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.SickLeaveLength;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.CasesPerMonthData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

@Service("protectedChartService")
@Path("/verksamhet")
public class ProtectedChartDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ProtectedChartDataService.class);

    private VerksamhetOverview datasourceOverview;
    private CasesPerMonth datasourceCasesPerMonth;
    private DiagnosisGroups datasourceDiagnosisGroups;
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;
    private AgeGroups datasourceAgeGroups;
    private DegreeOfSickLeave datasourceDegreeOfSickLeave;
    private SickLeaveLength datasourceSickLeaveLength;

    public ProtectedChartDataService() {

    }

    public ProtectedChartDataService(VerksamhetOverview overviewPersistenceHandler,
                                     CasesPerMonth casesPerMonthPersistenceHandler,
                                     DiagnosisGroups diagnosisGroupsPersistenceHandler,
                                     DiagnosisSubGroups diagnosisSubGroupsPersistenceHandler,
                                     AgeGroups ageGroupsPersistenceHandler,
                                     DegreeOfSickLeave degreeOfSickLeavePersistenceHandler,
                                     SickLeaveLength sickLeaveLengthPersistenceHandler) {
        datasourceOverview = overviewPersistenceHandler;
        datasourceCasesPerMonth = casesPerMonthPersistenceHandler;
        datasourceDiagnosisGroups = diagnosisGroupsPersistenceHandler;
        datasourceDiagnosisSubGroups = diagnosisSubGroupsPersistenceHandler;
        datasourceAgeGroups = ageGroupsPersistenceHandler;
        datasourceDegreeOfSickLeave = degreeOfSickLeavePersistenceHandler;
        datasourceSickLeaveLength = sickLeaveLengthPersistenceHandler;
    }

    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public CasesPerMonthData getNumberOfCasesPerMonth(@PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getNumberOfCasesPerMonth with verksamhetId: " + verksamhetId);
        Range range = new Range();
        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(Verksamhet.decodeId(verksamhetId), range);
        return new CasesPerMonthConverter().convert(casesPerMonth);
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisGroupStatistics(@PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDiagnosisGroupStatistics with verksamhetId: " + verksamhetId);
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(verksamhetId, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisSubGroupStatistics/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@PathParam("verksamhetId") String verksamhetId, @PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosisSubGroupStatistics with verksamhetId: '" + verksamhetId + "' and groupId: " + groupId);
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
        LOG.info("Calling getAgeGroupsStatistics with verksamhetId: " + verksamhetId);
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getAgeGroups("hsaid", new LocalDate().minusMonths(1));
        return new AgeGroupsConverter().convert(ageGroups);
    }

    @GET
    @Path("{verksamhetId}/getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics(@PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics with verksamhetId: " + verksamhetId);
        DegreeOfSickLeaveResponse degreeOfSickLeaveStatistics = datasourceDegreeOfSickLeave.getStatistics(Verksamhet.decodeId(verksamhetId));
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics);
    }

    @GET
    @Path("{verksamhetId}/getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    public SickLeaveLengthData getSickLeaveLengthData(@PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthData with verksamhetId: " + verksamhetId);
        final int numberOfMonthsToRequest = 12;
        Range range = new Range(numberOfMonthsToRequest);
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getStatistics(Verksamhet.decodeId(verksamhetId), range);
        return new SickLeaveLengthConverter().convert(sickLeaveLength);
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

    @SuppressWarnings("unchecked")
    private List<Verksamhet> getVerksamhets(HttpServletRequest request) {
        return (List<Verksamhet>) request.getSession().getAttribute("verksamhets");
    }
}
