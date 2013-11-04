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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.SimpleDetailsData;
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
    private SjukfallslangdGrupp datasourceSickLeaveLength;
    public final Helper helper = new Helper();

    public ProtectedChartDataService() {

    }

    public ProtectedChartDataService(VerksamhetOverview overviewPersistenceHandler,
                                     CasesPerMonth casesPerMonthPersistenceHandler,
                                     DiagnosisGroups diagnosisGroupsPersistenceHandler,
                                     DiagnosisSubGroups diagnosisSubGroupsPersistenceHandler,
                                     AgeGroups ageGroupsPersistenceHandler,
                                     DegreeOfSickLeave degreeOfSickLeavePersistenceHandler,
                                     SjukfallslangdGrupp sickLeaveLengthPersistenceHandler) {
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
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getNumberOfCasesPerMonth(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getNumberOfCasesPerMonth with verksamhetId: " + verksamhetId);
        Range range = new Range();
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth(Verksamhet.decodeId(verksamhetId), range);
        return new SimpleDualSexConverter().convert(casesPerMonth);
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDiagnosisGroupStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDiagnosisGroupStatistics with verksamhetId: " + verksamhetId);
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups(verksamhetId, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisSubGroupStatistics/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId, @PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosisSubGroupStatistics with verksamhetId: '" + verksamhetId + "' and groupId: " + groupId);
        Range range = new Range();
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisGroups(Verksamhet.decodeId(verksamhetId), range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("{verksamhetId}/getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public VerksamhetOverviewData getOverviewData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        Range range = Range.quarter();
        VerksamhetOverviewResponse response = datasourceOverview.getOverview(Verksamhet.decodeId(verksamhetId), range);
        return new VerksamhetOverviewConverter().convert(response, range);
    }

    @GET
    @Path("{verksamhetId}/getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public AgeGroupsData getAgeGroupsStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getAgeGroupsStatistics with verksamhetId: " + verksamhetId);
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getHistoricalAgeGroups(Verksamhet.decodeId(verksamhetId), Helper.previousMonth(), RollingLength.QUARTER);
        return new AgeGroupsConverter().convert(ageGroups);
    }

    @GET
    @Path("{verksamhetId}/getAgeGroupsCurrentStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public AgeGroupsData getAgeGroupsCurrentStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getAgeGroupsCurrentStatistics with verksamhetId: " + verksamhetId);
        AgeGroupsResponse ageGroups = datasourceAgeGroups.getCurrentAgeGroups(Verksamhet.decodeId(verksamhetId));
        return new AgeGroupsConverter().convert(ageGroups);
    }

    @GET
    @Path("{verksamhetId}/getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics with verksamhetId: " + verksamhetId);
        DegreeOfSickLeaveResponse degreeOfSickLeaveStatistics = datasourceDegreeOfSickLeave.getStatistics(Verksamhet.decodeId(verksamhetId), new Range());
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics);
    }

    @GET
    @Path("{verksamhetId}/getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthData with verksamhetId: " + verksamhetId);
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getHistoricalStatistics(Verksamhet.decodeId(verksamhetId), Helper.previousMonth(),
                RollingLength.YEAR);
        return new SickLeaveLengthConverter().convert(sickLeaveLength);
    }

    @GET
    @Path("{verksamhetId}/getSickLeaveLengthCurrentData")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthCurrentData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthCurrentData with verksamhetId: " + verksamhetId);
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getCurrentStatistics(Verksamhet.decodeId(verksamhetId));
        return new SickLeaveLengthConverter().convert(sickLeaveLength);
    }

    @GET
    @Path("{verksamhetId}/getLongSickLeavesData")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getLongSickLeavesData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getLongSickLeavesData with verksamhetId: " + verksamhetId);
        Range range = new Range();
        SimpleDualSexResponse<SimpleDualSexDataRow> longSickLeaves = datasourceSickLeaveLength.getLongSickLeaves(Verksamhet.decodeId(verksamhetId), range);
        return new SimpleDualSexConverter().convert(longSickLeaves);
    }

    protected static class Helper {

        public static boolean hasAccessTo(HttpServletRequest request, String verksamhetId) {
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

        public static boolean userAccess(HttpServletRequest request, String verksamhetId) {
            LOG.info("User " + request.getUserPrincipal().getName() + " accessed verksamhet " + verksamhetId + "(" + request.getRequestURI() + ")");
            return true;
        }

        @SuppressWarnings("unchecked")
        private static List<Verksamhet> getVerksamhets(HttpServletRequest request) {
            return (List<Verksamhet>) request.getSession().getAttribute("verksamhets");
        }

        private static LocalDate previousMonth() {
            return new LocalDate().withDayOfMonth(1).minusMonths(1);
        }
    }

}
