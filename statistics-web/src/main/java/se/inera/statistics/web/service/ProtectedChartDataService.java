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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.*;
import se.inera.statistics.service.report.api.Sjukskrivningsgrad;
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
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

@Service("protectedChartService")
@Path("/verksamhet")
public class ProtectedChartDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ProtectedChartDataService.class);

    @Autowired
    private VerksamhetOverview datasourceOverview;
    @Autowired
    private SjukfallPerManad datasourceSjukfallPerManad;
    @Autowired
    private Diagnosgrupp datasourceDiagnosgrupp;
    @Autowired
    private Diagnoskapitel datasourceDiagnoskapitel;
    @Autowired
    private Aldersgrupp datasourceAldersgrupp;
    @Autowired
    private Sjukskrivningsgrad datasourceSjukskrivningsgrad;
    @Autowired
    private SjukfallslangdGrupp datasourceSickLeaveLength;

    public final Helper helper = new Helper();

    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getNumberOfCasesPerMonth(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getNumberOfCasesPerMonth with verksamhetId: " + verksamhetId);
        final Range range = new Range(18);
        SimpleDualSexResponse<SimpleDualSexDataRow> casesPerMonth = datasourceSjukfallPerManad.getCasesPerMonth(Verksamhet.decodeId(verksamhetId), range);
        return new SimpleDualSexConverter().convert(casesPerMonth, range);
    }

    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerMonth/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerMonthAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getNumberOfCasesPerMonthAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getNumberOfCasesPerMonth(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDiagnosisGroupStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDiagnosisGroupStatistics with verksamhetId: " + verksamhetId);
        final Range range = new Range(18);
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosgrupp.getDiagnosisGroups(verksamhetId, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups, range);
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisGroupStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDiagnosisGroupStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getDiagnosisGroupStatistics(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisSubGroupStatistics/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId, @PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosisSubGroupStatistics with verksamhetId: '" + verksamhetId + "' and groupId: " + groupId);
        final Range range = new Range(18);
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnoskapitel.getDiagnosisGroups(Verksamhet.decodeId(verksamhetId), range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups, range);
    }

    @GET
    @Path("{verksamhetId}/getDiagnosisSubGroupStatistics/{groupId}/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisSubGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId, @PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosisSubGroupStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getDiagnosisSubGroupStatistics(request, verksamhetId, groupId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
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
        final RollingLength period = RollingLength.QUARTER;
        AgeGroupsResponse ageGroups = datasourceAldersgrupp.getHistoricalAgeGroups(Verksamhet.decodeId(verksamhetId), Helper.previousMonth(), period);
        return new AgeGroupsConverter().convert(ageGroups, new Range(period.getPeriods()));
    }

    @GET
    @Path("{verksamhetId}/getAgeGroupsStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsStatisticsAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getAgeGroupsStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getAgeGroupsStatistics(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("{verksamhetId}/getAgeGroupsCurrentStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public AgeGroupsData getAgeGroupsCurrentStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getAgeGroupsCurrentStatistics with verksamhetId: " + verksamhetId);
        AgeGroupsResponse ageGroups = datasourceAldersgrupp.getCurrentAgeGroups(Verksamhet.decodeId(verksamhetId));
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = new LocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);
        return new AgeGroupsConverter().convert(ageGroups, range);
    }

    @GET
    @Path("{verksamhetId}/getAgeGroupsCurrentStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsCurrentStatisticsAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getAgeGroupsCurrentStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getAgeGroupsCurrentStatistics(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("{verksamhetId}/getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics with verksamhetId: " + verksamhetId);
        final Range range = new Range(18);
        DegreeOfSickLeaveResponse degreeOfSickLeaveStatistics = datasourceSjukskrivningsgrad.getStatistics(Verksamhet.decodeId(verksamhetId), range);
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, range);
    }

    @GET
    @Path("{verksamhetId}/getDegreeOfSickLeaveStatistics/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getDegreeOfSickLeaveStatisticsAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getDegreeOfSickLeaveStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getDegreeOfSickLeaveStatistics(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("{verksamhetId}/getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthData with verksamhetId: " + verksamhetId);
        final RollingLength year = RollingLength.YEAR;
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getHistoricalStatistics(Verksamhet.decodeId(verksamhetId), Helper.previousMonth(),
                year);
        return new SickLeaveLengthConverter().convert(sickLeaveLength, new Range(year.getPeriods()));
    }

    @GET
    @Path("{verksamhetId}/getSickLeaveLengthData/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthDataAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthDataAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getSickLeaveLengthData(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("{verksamhetId}/getSickLeaveLengthCurrentData")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthCurrentData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthCurrentData with verksamhetId: " + verksamhetId);
        SickLeaveLengthResponse sickLeaveLength = datasourceSickLeaveLength.getCurrentStatistics(Verksamhet.decodeId(verksamhetId));
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = new LocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);
        return new SickLeaveLengthConverter().convert(sickLeaveLength, range);
    }

    @GET
    @Path("{verksamhetId}/getSickLeaveLengthCurrentData/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthCurrentDataAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthCurrentDataAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getSickLeaveLengthCurrentData(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @GET
    @Path("{verksamhetId}/getLongSickLeavesData")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getLongSickLeavesData(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getLongSickLeavesData with verksamhetId: " + verksamhetId);
        final Range range = new Range(18);
        SimpleDualSexResponse<SimpleDualSexDataRow> longSickLeaves = datasourceSickLeaveLength.getLongSickLeaves(Verksamhet.decodeId(verksamhetId), range);
        return new SimpleDualSexConverter().convert(longSickLeaves, range);
    }

    @GET
    @Path("{verksamhetId}/getLongSickLeavesData/csv")
    @Produces({ "text/plain; charset=UTF-8" })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getLongSickLeavesDataAsCsv(@Context HttpServletRequest request, @PathParam("verksamhetId") String verksamhetId) {
        LOG.info("Calling getLongSickLeavesDataAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getLongSickLeavesData(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    public Helper getHelper() {
        return helper;
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
            LOG.info("User " + ServiceUtil.getLoginInfo(request).getHsaId() + " accessed verksamhet " + verksamhetId + " (" + getUriSafe(request) + ") session " + request.getSession().getId());
            return true;
        }

        private static String getUriSafe(HttpServletRequest request) {
            if (request == null) {
                return "!NoRequest!";
            }
            return request.getRequestURI();
        }

        private static List<Verksamhet> getVerksamhets(HttpServletRequest request) {
            return ServiceUtil.getLoginInfo(request).getBusinesses();
        }

        private static LocalDate previousMonth() {
            return new LocalDate().withDayOfMonth(1).minusMonths(1);
        }
    }

}
