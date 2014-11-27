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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
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
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static final String KAPITEL_STRING = "kapitel";
    private static final String AVSNITT_STRING = "avsnitt";
    private static final String KATEGORI_STRING = "kategorier";

    private static final Splitter ID_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    @Autowired
    private WarehouseService warehouse;

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    /**
     * Gets sjukfall per manad for verksamhetId.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getNumberOfCasesPerMonth(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getNumberOfCasesPerMonth with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerMonthAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getNumberOfCasesPerMonthAsCsv with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final TableData tableData = getNumberOfCasesPerMonth(request, verksamhetId, idString, kapitelString, avsnittString, kategoriString).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Gets sjukfall per enhet for verksamhetId.
     */
    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerEnhet")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getNumberOfCasesPerEnhet(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getNumberOfCasesPerEnhet with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final Range range = new Range(12);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
        Map<String, String> idToNameMap = getEnhetNameMap(request, verksamhet, getIdsFromIdString(idString));
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerEnhet(filter, idToNameMap, range, verksamhet.getVardgivarId());
        return new GroupedSjukfallConverter("Vårdenhet").convert(casesPerEnhet, range);
    }

    /**
     * Gets sjukfall per doctor for verksamhetId.
     */
    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerLakare")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getNumberOfCasesPerLakare(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getNumberOfCasesPerLakare with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final Range range = new Range(12);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
        SimpleKonResponse<SimpleKonDataRow> casesPerLakare = warehouse.getCasesPerLakare(filter, range, verksamhet.getVardgivarId());
        return new GroupedSjukfallConverter("Läkare").convert(casesPerLakare, range);
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDiagnosisGroupStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString) {
        LOG.info("Calling getDiagnoskapitelstatistik with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), null, null, null);
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString) {
        LOG.info("Calling getDiagnoskapitelstatistikAsCsv with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final TableData tableData = getDiagnosisGroupStatistics(request, verksamhetId, idString).getTableData();
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @PathParam("groupId") String groupId, @QueryParam(ID_STRING) String idString) {
        LOG.info("Calling getDiagnosavsnittstatistik with verksamhetId: {} and groupId: {} and ids: {}", verksamhetId, groupId, idString);
        try {
            return Response.ok(getDiagnosisSubGroupStatisticsEntity(request, verksamhetId, groupId, idString)).build();
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private DualSexStatisticsData getDiagnosisSubGroupStatisticsEntity(HttpServletRequest request, String verksamhetId, String groupId, String idString) throws RangeNotFoundException {
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), null, null, null);
        final String vardgivarId = verksamhet.getVardgivarId();
        DiagnosgruppResponse diagnosavsnitt = warehouse.getUnderdiagnosgrupper(filter, range, groupId, vardgivarId);
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisSubGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @PathParam("groupId") String groupId, @QueryParam(ID_STRING) String idString) {
        LOG.info("Calling getDiagnosavsnittstatistikAsCsv with verksamhetId: {} and groupId: {} and ids: {}", verksamhetId, groupId, idString);
        final TableData tableData;
        try {
            tableData = getDiagnosisSubGroupStatisticsEntity(request, verksamhetId, groupId, idString).getTableData();
            return CsvConverter.getCsvResponse(tableData, "export.csv");
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public VerksamhetOverviewData getOverviewData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getOverview with verksamhetId: {} and ids: {}", verksamhetId, idString);
        Range range = Range.quarter();
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public AgeGroupsData getAgeGroupsStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getAgeGroupsStatistics with verksamhetId: {} and idString: {}", verksamhetId, idString);
        final Range range = new Range(12);

        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getAgeGroupsStatisticsAsCsv with verksamhetId: {} and idString: {}", verksamhetId, idString);
        final TableData tableData = getAgeGroupsStatistics(request, verksamhetId, idString, kapitelString, avsnittString, kategoriString).getTableData();
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public AgeGroupsData getAgeGroupsCurrentStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getAgeGroupsCurrentStatistics with verksamhetId: {} and ids: {}", verksamhetId, idString);
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = new LocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);

        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsCurrentStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getAgeGroupsCurrentStatisticsAsCsv with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final TableData tableData = getAgeGroupsCurrentStatistics(request, verksamhetId, idString, kapitelString, avsnittString, kategoriString).getTableData();
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDegreeOfSickLeaveStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getDegreeOfSickLeaveStatisticsAsCsv with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final TableData tableData = getDegreeOfSickLeaveStatistics(request, verksamhetId, idString, kapitelString, avsnittString, kategoriString).getTableData();
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getSickLeaveLengthData with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final RollingLength year = RollingLength.YEAR;
        Range range = new Range(year.getPeriods());
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getSickLeaveLengthDataAsCsv with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final TableData tableData = getSickLeaveLengthData(request, verksamhetId, idString, kapitelString, avsnittString, kategoriString).getTableData();
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public SickLeaveLengthData getSickLeaveLengthCurrentData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getSickLeaveLengthCurrentData with verksamhetId: {} and ids: {}", verksamhetId, idString);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthCurrentDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getSickLeaveLengthCurrentDataAsCsv with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final TableData tableData = getSickLeaveLengthCurrentData(request, verksamhetId, idString, kapitelString, avsnittString, kategoriString).getTableData();
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
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public SimpleDetailsData getLongSickLeavesData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getLongSickLeavesData with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final Range range = new Range(18);
        Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
        Predicate<Fact> filter = getFilter(request, verksamhet, getIdsFromIdString(idString), getIdsFromIdString(kapitelString), getIdsFromIdString(avsnittString), getIdsFromIdString(avsnittString));
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
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getLongSickLeavesDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam(ID_STRING) String idString, @QueryParam(KAPITEL_STRING) String kapitelString, @QueryParam(AVSNITT_STRING) String avsnittString, @QueryParam(KATEGORI_STRING) String kategoriString) {
        LOG.info("Calling getLongSickLeavesDataAsCsv with verksamhetId: {} and ids: {}", verksamhetId, idString);
        final TableData tableData = getLongSickLeavesData(request, verksamhetId, idString, kapitelString, avsnittString, kategoriString).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    Predicate<Fact> getFilter(HttpServletRequest request, Verksamhet verksamhet, List<String> enhetsIDs, List<String> kapitelIDs, List<String> avsnittIDs, List<String> kategoriIDs) {
        Predicate<Fact> enhetFilter = getEnhetFilter(request, verksamhet, enhetsIDs);
        Predicate<Fact> diagnosFilter = getDiagnosFilter(kapitelIDs, avsnittIDs, kategoriIDs);
        return Predicates.and(enhetFilter, diagnosFilter);
    }

    private Predicate<Fact> getDiagnosFilter(final List<String> kapitelIDs, final List<String> avsnittIDs, final List<String> kategoriIDs) {
        return new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                if (kapitelIDs != null && kapitelIDs.contains(String.valueOf(fact.getDiagnoskapitel()))) {
                    return true;
                } else if (avsnittIDs != null && avsnittIDs.contains(String.valueOf(fact.getDiagnosavsnitt()))) {
                    return true;
                } else {
                    return kategoriIDs != null && kategoriIDs.contains(String.valueOf(fact.getDiagnoskategori()));
                }
            }
        };
    }

    SjukfallUtil.EnhetFilter getEnhetFilter(HttpServletRequest request, Verksamhet
            verksamhet, List<String> enhetsIDs) {
        Set<String> enheter = getEnhetNameMap(request, verksamhet, enhetsIDs).keySet();
        return SjukfallUtil.createEnhetFilter(enheter.toArray(new String[enheter.size()]));
    }

    private Map<String, String> getEnhetNameMap(HttpServletRequest request, Verksamhet
            verksamhet, List<String> enhetsIDs) {

        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        Map<String, String> enheter = new HashMap<>();
        for (Verksamhet userVerksamhet : info.getBusinesses()) {
            if (userVerksamhet.getVardgivarId().equals(verksamhet.getVardgivarId())) {
                if (enhetsIDs == null || enhetsIDs.contains(userVerksamhet.getId())) {
                    enheter.put(userVerksamhet.getId(), userVerksamhet.getName());
                }
            }
        }
        return enheter;
    }

    private List<String> getIdsFromIdString(String ids) {
        if (ids != null) {
            return ID_SPLITTER.splitToList(ids);
        }
        return null;
    }

    private Verksamhet getVerksamhet(HttpServletRequest request, String verksamhetId) {
        List<Verksamhet> verksamhets = loginServiceUtil.getLoginInfo(request).getBusinesses();
        for (Verksamhet verksamhet : verksamhets) {
            if (verksamhet.getVardgivarId().equals(verksamhetId)) {
                return verksamhet;
            }
        }
        return null;
    }

    public boolean hasAccessTo(HttpServletRequest request, String verksamhetId) {
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

    public boolean userAccess(HttpServletRequest request, String verksamhetId) {
        LOG.info("User " + loginServiceUtil.getLoginInfo(request).getHsaId() + " accessed verksamhet " + verksamhetId + " (" + getUriSafe(request) + ") session " + request.getSession().getId());
        return true;
    }

    private String getUriSafe(HttpServletRequest request) {
        if (request == null) {
            return "!NoRequest!";
        }
        return request.getRequestURI();
    }

    private List<Verksamhet> getVerksamhets(HttpServletRequest request) {
        return loginServiceUtil.getLoginInfo(request).getBusinesses();
    }

}
