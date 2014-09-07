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

import com.google.common.annotations.VisibleForTesting;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import se.inera.statistics.service.report.api.RollingLength;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Statistics services that requires authorization to use. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv variants
 * only contains one data set.
 * <p/>
 * They all return 403 if called outside of a session or if authorization fails.
 */
@Service("protectedChartService")
@Path("/verksamhet")
public class ProtectedChartDataService {

    private static final String VERKSAMHET_PATH_ID = "verksamhetId";

    private static final Logger LOG = LoggerFactory.getLogger(ProtectedChartDataService.class);
    private static final String TEXT_UTF_8 = "text/plain; charset=UTF-8";
    private static final String ID_STRING = "ids";

    @Autowired
    private WarehouseService warehouse;

    public final Helper helper = new Helper();

    /**
     * Gets sjukfall per manad for verksamhetId.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerMonth")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getNumberOfCasesPerMonth(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getNumberOfCasesPerMonth with verksamhetId: " + verksamhetId);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonth(filter, range, verksamhet.getVardgivarId());
        return new SimpleDualSexConverter().convert(casesPerMonth, range);
    }

    /**
     * Gets sjukfall per manad for verksamhetId, csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerMonth/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerMonthAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getNumberOfCasesPerMonthAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getNumberOfCasesPerMonth(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfall per diagnoskapitel and per diagnosgrupp. The chart data is grouped by diagnosgrupp,
     * the table data by diagnoskapitel. Diagnosgrupp is a diagnoskapitel or a list of diagnoskapitel.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getDiagnoskapitelstatistik")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDiagnosisGroupStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getDiagnoskapitelstatistik with verksamhetId: " + verksamhetId);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        DiagnosgruppResponse diagnosisGroups = warehouse.getDiagnosgrupperPerMonth(filter, range, verksamhet.getVardgivarId());
        return new DiagnosisGroupsConverter().convert(diagnosisGroups, range);
    }

    /**
     * Get sjukfall per diagnoskapitel. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getDiagnoskapitelstatistik/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getDiagnoskapitelstatistikAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getDiagnosisGroupStatistics(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getDiagnosavsnittstatistik/{groupId}")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosavsnittstatistik with verksamhetId: '" + verksamhetId + "' and groupId: " + groupId);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        DiagnosgruppResponse diagnosavsnitt = warehouse.getDiagnosavsnitt(filter, range, groupId, verksamhet.getVardgivarId());
        return new DiagnosisSubGroupsConverter().convert(diagnosavsnitt, range);
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getDiagnosavsnittstatistik/{groupId}/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisSubGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosavsnittstatistikAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getDiagnosisSubGroupStatistics(request, verksamhetId, groupId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get overview. Includes total n:o of sjukfall, sex distribution, top lists for diagnosgrupp, aldersgrupp, sjukskrivningslangd,
     * sjukskrivningsgrad. Only chart formatted data.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getOverview")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public VerksamhetOverviewData getOverviewData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getOverview with verksamhetId: " + verksamhetId);
        Range range = Range.quarter();
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        VerksamhetOverviewResponse response = warehouse.getOverview(filter, range, verksamhet.getVardgivarId());

        return new VerksamhetOverviewConverter().convert(response, range);
    }

    /**
     * Get sjukfall grouped by age and sex.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getAgeGroupsStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public AgeGroupsData getAgeGroupsStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getAgeGroupsStatistics with verksamhetId: " + verksamhetId);
        final Range range = new Range(12);

        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter, range, verksamhet.getVardgivarId());
        return new AgeGroupsConverter().convert(ageGroups, range);
    }

    /**
     * Get sjukfall grouped by age and sex. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getAgeGroupsStatistics/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getAgeGroupsStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getAgeGroupsStatistics(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get ongoing sjukfall grouped by age and sex.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getAgeGroupsCurrentStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public AgeGroupsData getAgeGroupsCurrentStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getAgeGroupsCurrentStatistics with verksamhetId: " + verksamhetId);
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = new LocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);

        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter, range, verksamhet.getVardgivarId());
        return new AgeGroupsConverter().convert(ageGroups, range);

    }

    /**
     * Get ongoing sjukfall grouped by age and sex. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getAgeGroupsCurrentStatistics/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsCurrentStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getAgeGroupsCurrentStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getAgeGroupsCurrentStatistics(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getDegreeOfSickLeaveStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        SjukskrivningsgradResponse degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradPerMonth(filter, range, verksamhet.getVardgivarId());
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, range);
    }

    /**
     * Get sjukskrivningsgrad per calendar month. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getDegreeOfSickLeaveStatistics/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getDegreeOfSickLeaveStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getDegreeOfSickLeaveStatisticsAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getDegreeOfSickLeaveStatistics(request, verksamhetId, null).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfallslangd (grouped).
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthData with verksamhetId: " + verksamhetId);
        final RollingLength year = RollingLength.YEAR;
        Range range = new Range(year.getPeriods());
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        SjukfallslangdResponse sickLeaveLength = warehouse.getSjukskrivningslangd(filter, range, verksamhet.getVardgivarId());
        return new SickLeaveLengthConverter().convert(sickLeaveLength, range);
    }

    /**
     * Get sjukfallslangd (grouped). Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getSickLeaveLengthData/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthDataAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getSickLeaveLengthData(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfallslangd (grouped) for current month.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getSickLeaveLengthCurrentData")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthCurrentData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthCurrentData with verksamhetId: " + verksamhetId);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        SjukfallslangdResponse sickLeaveLength = warehouse.getSjukskrivningslangd(filter, range, verksamhet.getVardgivarId());
        return new SickLeaveLengthConverter().convert(sickLeaveLength, range);
    }

    /**
     * Get sjukfallslangd (grouped) for current month. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getSickLeaveLengthCurrentData/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthCurrentDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getSickLeaveLengthCurrentDataAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getSickLeaveLengthCurrentData(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Gets sjukfallslangd, grouped by sex, long / not long sjukfall.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getLongSickLeavesData")
    @Produces({MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getLongSickLeavesData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getLongSickLeavesData with verksamhetId: " + verksamhetId);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        SjukfallUtil.StartFilter filter = getFilter(request, verksamhet);
        SimpleKonResponse<SimpleKonDataRow> longSickLeaves = warehouse.getLangaSjukskrivningarPerManad(filter, range, verksamhet.getVardgivarId());
        return new SimpleDualSexConverter().convert(longSickLeaves, range);
    }

    /**
     * Gets sjukfallslangd, grouped by sex, long / not long sjukfall. Csv formatted
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getLongSickLeavesData/csv")
    @Produces({ TEXT_UTF_8 })
    @PreAuthorize(value = "@protectedChartDataService.helper.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.helper.userAccess(#request, #verksamhetId)")
    public Response getLongSickLeavesDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId) {
        LOG.info("Calling getLongSickLeavesDataAsCsv with verksamhetId: " + verksamhetId);
        final TableData tableData = getLongSickLeavesData(request, verksamhetId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    @VisibleForTesting
    private SjukfallUtil.StartFilter getFilter(HttpServletRequest request, Verksamhet verksamhet) {
        LoginInfo info = ServiceUtil.getLoginInfo(request);
        SjukfallUtil.StartFilter filter;
        if (info.isFullVgAccess()) {
            filter = SjukfallUtil.ALL_ENHETER;
        } else {
            List<String> enheter = new ArrayList<>();
            for (Verksamhet v : info.getBusinesses()) {
                if (v.getVardgivarId().equals(verksamhet.getVardgivarId())) {
                    enheter.add(v.getId());
                }
            }
            filter = SjukfallUtil.createEnhetFilter(enheter.toArray(new String[enheter.size()]));
        }
        return filter;
    }

    public Helper getHelper() {
        return helper;
    }

    private Verksamhet getVerksamhet(HttpServletRequest request, String verksamhetId) {
        List<Verksamhet> verksamhets = ServiceUtil.getLoginInfo(request).getBusinesses();
        for (Verksamhet verksamhet: verksamhets) {
            if (verksamhet.getVardgivarId().equals(verksamhetId)) {
                return verksamhet;
            }
        }
        return null;
    }

    protected static class Helper {

        public static boolean hasAccessTo(HttpServletRequest request, String verksamhetId) {
            if (request == null || verksamhetId == null) {
                return false;
            }
            List<Verksamhet> verksamhets = getVerksamhets(request);
            if (verksamhets != null) {
                for (Verksamhet verksamhet : verksamhets) {
                    if (verksamhetId.equals(verksamhet.getVardgivarId())) {
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
    }
}
