/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    private static final Logger LOG = LoggerFactory.getLogger(ProtectedChartDataService.class);
    private static final String TEXT_UTF_8 = "text/plain; charset=UTF-8";
    public static final int YEAR = 12;

    @Autowired
    private WarehouseService warehouse;

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    @Autowired
    private FilterHashHandler filterHashHandler;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private ResultMessageHandler resultMessageHandler;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    /**
     * Gets sjukfall per manad for verksamhetId.
     */
    @POST
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerMonth(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerMonth with filterHash: {}", filterHash);
        SimpleDetailsData result = getNumberOfCasesPerMonthData(request, filterHash);
        return Response.ok(result).build();
    }

    private SimpleDetailsData getNumberOfCasesPerMonthData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getNumberOfCasesPerMonth with filterHash: {}", filterHash);
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonth(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        return new PeriodConverter().convert(casesPerMonth, range, filter);
    }

    /**
     * Gets sjukfall per manad for verksamhetId, csv formatted.
     */
    @GET
    @Path("getNumberOfCasesPerMonth/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerMonthAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerMonthAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData data = getNumberOfCasesPerMonthData(request, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    /**
     * Gets sjukfall per enhet for verksamhetId.
     */
    @POST
    @Path("getNumberOfCasesPerEnhet")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhet(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerEnhet with filterHash: {}", filterHash);
        SimpleDetailsData result = getNumberOfCasesPerEnhetData(request, filterHash);
        return Response.ok(result).build();
    }

    private SimpleDetailsData getNumberOfCasesPerEnhetData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getNumberOfCasesPerEnhet with filterHash: {}", filterHash);
        final Range range = new Range(12);
        Filter filter = getFilter(request, filterHash);
        Map<String, String> idToNameMap = getEnhetNameMap(request, getEnhetsFilterIds(filterHash, request));
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerEnhet(filter.getPredicate(), idToNameMap, range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new GroupedSjukfallConverter("Vårdenhet").convert(casesPerEnhet, range, filter);
        return result;
    }

    /**
     * Gets sjukfall per enhet for verksamhetId, csv formatted.
     */
    @GET
    @Path("getNumberOfCasesPerEnhet/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhetAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerEnhetAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData data = getNumberOfCasesPerEnhetData(request, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    /**
     * Gets sjukfall per enhet for verksamhetId i tidsserie.
     */
    @POST
    @Path("getNumberOfCasesPerEnhetTimeSeries")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhetTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerEnhetTimeSeries with filterHash: {}", filterHash);
        DualSexStatisticsData result = getNumberOfCasesPerEnhetDataTimeSeries(request, filterHash);
        return Response.ok(result).build();
    }

    private DualSexStatisticsData getNumberOfCasesPerEnhetDataTimeSeries(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getNumberOfCasesPerEnhetTidsserie with filterHash: {}", filterHash);
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        Map<String, String> idToNameMap = getEnhetNameMap(request, getEnhetsFilterIds(filterHash, request));
        KonDataResponse casesPerEnhet = warehouse.getCasesPerEnhetTimeSeries(filter.getPredicate(), idToNameMap, range, getSelectedVgIdForLoggedInUser(request));

        DualSexStatisticsData result = new SimpleMultiDualSexConverter().convert(casesPerEnhet, range, filter);
        return result;
    }

    private List<String> getEnhetsFilterIds(String filterHash, HttpServletRequest request) {
        if (filterHash == null || filterHash.isEmpty()) {
            final LoginInfo info = loginServiceUtil.getLoginInfo(request);
            final List<Verksamhet> businesses = info.getBusinesses();
            return Lists.transform(businesses, new Function<Verksamhet, String>() {
                @Override
                public String apply(Verksamhet verksamhet) {
                    return verksamhet.getId();
                }
            });
        }
        final FilterData filterData = getFilterFromHash(filterHash);
        return getEnhetsFiltered(request, filterData);
    }

    /**
     * Gets sjukfall per doctor for verksamhetId.
     */
    @POST
    @Path("getNumberOfCasesPerLakare")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakare(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerLakare with filterHash: {}", filterHash);
        final SimpleDetailsData result = getNumberOfCasesPerLakareData(request, filterHash);
        return Response.ok(result).build();
    }

    private SimpleDetailsData getNumberOfCasesPerLakareData(HttpServletRequest request, String filterHash) {
        final Range range = new Range(12);
        Filter filter = getFilter(request, filterHash);
        SimpleKonResponse<SimpleKonDataRow> casesPerLakare = warehouse.getCasesPerLakare(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new GroupedSjukfallConverter("Läkare").convert(casesPerLakare, range, filter);
        return result;
    }

    /**
     * Gets sjukfall per doctor for verksamhetId. Csv formatted.
     */
    @GET
    @Path("getSjukfallPerLakareVerksamhet/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakareAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerLakareAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData data = getNumberOfCasesPerLakareData(request, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    /**
     * Get sjukfall per diagnoskapitel and per diagnosgrupp. The chart data is grouped by diagnosgrupp,
     * the table data by diagnoskapitel. Diagnosgrupp is a diagnoskapitel or a list of diagnoskapitel.
     */
    @POST
    @Path("getDiagnoskapitelstatistik")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisGroupStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnoskapitelstatistik with filterHash: {}", filterHash);
        DualSexStatisticsData result = getDiagnosisGroupStatisticsData(request, filterHash);
        return Response.ok(result).build();
    }

    private DualSexStatisticsData getDiagnosisGroupStatisticsData(HttpServletRequest request, String filterHash) {
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        DiagnosgruppResponse diagnosisGroups = warehouse.getDiagnosgrupperPerMonth(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new DiagnosisGroupsConverter().convert(diagnosisGroups, range, filter);
        return result;
    }

    /**
     * Get sjukfall per diagnoskapitel. Csv formatted.
     */
    @GET
    @Path("getDiagnoskapitelstatistik/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisGroupStatisticsAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnoskapitelstatistikAsCsv with filterHash: {}", filterHash);
        final DualSexStatisticsData data = getDiagnosisGroupStatisticsData(request, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     */
    @POST
    @Path("getDiagnosavsnittstatistik/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam("groupId") String groupId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnosavsnittstatistik with groupId: {} and filterHash: {}", groupId, filterHash);
        try {
            DualSexStatisticsData data = getDiagnosisSubGroupStatisticsEntity(request, groupId, filterHash);
            return Response.ok(data).build();
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private DualSexStatisticsData getDiagnosisSubGroupStatisticsEntity(HttpServletRequest request, String groupId, String filterHash) throws RangeNotFoundException {
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        DiagnosgruppResponse diagnosavsnitt = warehouse.getUnderdiagnosgrupper(filter.getPredicate(), range, groupId, getSelectedVgIdForLoggedInUser(request));
        final String message = getDiagnosisSubGroupStatisticsMessage(filter, Arrays.asList(String.valueOf(icd10.findFromIcd10Code(groupId).toInt())));
        DualSexStatisticsData result = new DiagnosisSubGroupsConverter().convert(diagnosavsnitt, range, filter, message);
        return result;
    }

    private String getDiagnosisSubGroupStatisticsMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {
            return "Du har gjort ett val av diagnoskapitel eller diagnosavsnitt som inte matchar det val du gjort i diagnosfilter (se Visa filter högst upp på sidan).";
        }
        return null;
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel. Csv formatted.
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisSubGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam("groupId") String groupId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnosavsnittstatistikAsCsv with groupId: {} and filterHash: {}", groupId, filterHash);
        try {
            final DualSexStatisticsData data = getDiagnosisSubGroupStatisticsEntity(request, groupId, filterHash);
            return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("getJamforDiagnoserStatistik/{diagnosHash}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatistics(@Context HttpServletRequest request, @PathParam("diagnosHash") String diagnosisHash, @QueryParam("filter") String filterHash) {
        SimpleDetailsData data = getCompareDiagnosisStatisticsData(request, diagnosisHash, filterHash);
        return Response.ok(data).build();
    }

    private SimpleDetailsData getCompareDiagnosisStatisticsData(HttpServletRequest request, String diagnosisHash, String filterHash) {
        LOG.info("Calling getCompareDiagnosisStatistics with diagnosis: {} and filterHash: {}", diagnosisHash, filterHash);
        final Range range = new Range(12);
        Filter filter = getFilter(request, filterHash);
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String>emptyList() : getFilterFromHash(diagnosisHash).getDiagnoser();
        final String message = emptyDiagnosisHash ? "Inga diagnoser valda" : getCompareDiagnosisMessage(filter, diagnosis);
        SimpleKonResponse<SimpleKonDataRow> resultRows = warehouse.getJamforDiagnoser(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request), diagnosis);
        SimpleDetailsData result = new CompareDiagnosisConverter().convert(resultRows, range, filter, message);
        return result;
    }

    @GET
    @Path("getJamforDiagnoserStatistik/{diagnosHash}/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatisticsAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("diagnosHash") String diagnosisHash) {
        LOG.info("Calling getCompareDiagnosisStatisticsAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData data = getCompareDiagnosisStatisticsData(request, diagnosisHash, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    private String getCompareDiagnosisMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {
            return "Du har gjort ett val av diagnos som inte matchar det val du gjort i diagnosfilter (se Visa filter högst upp på sidan).";
        }
        return null;
    }

    @POST
    @Path("getJamforDiagnoserStatistikTidsserie/{diagnosHash}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatisticsTimeSeries(@Context HttpServletRequest request, @PathParam("diagnosHash") String diagnosisHash, @QueryParam("filter") String filterHash) {
        DualSexStatisticsData data = getCompareDiagnosisStatisticsDataTimeSeries(request, diagnosisHash, filterHash);
        return Response.ok(data).build();
    }

    private DualSexStatisticsData getCompareDiagnosisStatisticsDataTimeSeries(HttpServletRequest request, String diagnosisHash, String filterHash) {
        LOG.info("Calling getCompareDiagnosisStatisticsTimeSeries with diagnosis: {} and filterHash: {}", diagnosisHash, filterHash);
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String>emptyList() : getFilterFromHash(diagnosisHash).getDiagnoser();
        final String message = emptyDiagnosisHash ? "Inga diagnoser valda" : getCompareDiagnosisMessage(filter, diagnosis);
        KonDataResponse resultRows = warehouse.getJamforDiagnoserTidsserie(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request), diagnosis);
        DualSexStatisticsData result = new CompareDiagnosisTimeSeriesConverter().convert(resultRows, range, filter, message);
        return result;
    }

    @GET
    @Path("getJamforDiagnoserStatistikTidsserie/{diagnosHash}/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatisticsTimeSeriesAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("diagnosHash") String diagnosisHash) {
        LOG.info("Calling getCompareDiagnosisStatisticsTimeSeriesAsCsv with filterHash: {}", filterHash);
        final DualSexStatisticsData data = getCompareDiagnosisStatisticsDataTimeSeries(request, diagnosisHash, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    /**
     * Get overview. Includes total n:o of sjukfall, sex distribution, top lists for diagnosgrupp, aldersgrupp, sjukskrivningslangd,
     * sjukskrivningsgrad. Only chart formatted data.
     */
    @POST
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getOverviewData(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getOverview with filterHash: {}", filterHash);
        final Range range = Range.quarter();
        Filter filter = getFilter(request, filterHash);
        VerksamhetOverviewResponse response = warehouse.getOverview(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        return Response.ok(new VerksamhetOverviewConverter().convert(response, range, filter)).build();
    }

    /**
     * Get sjukfall grouped by age and sex.
     */
    @POST
    @Path("getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        SimpleDetailsData data = getAgeGroupsStatisticsData(request, filterHash);
        return Response.ok(data).build();
    }

    private SimpleDetailsData getAgeGroupsStatisticsData(HttpServletRequest request, String filterHash) {
            LOG.info("Calling getAgeGroupsStatistics with filterHash: {}", filterHash);
            final Range range = new Range(12);
            Filter filter = getFilter(request, filterHash);
            SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
            SimpleDetailsData result = new AgeGroupsConverter().convert(ageGroups, range, filter);
            return result;
    }

    /**
     * Get sjukfall grouped by age and sex. Csv formatted.
     */
    @GET
    @Path("getAgeGroupsStatistics/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatisticsAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getAgeGroupsStatisticsAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData simpleDetailsData = getAgeGroupsStatisticsData(request, filterHash);
        return CsvConverter.getCsvResponse(simpleDetailsData.getTableData(), "export.csv");
    }

    @POST
    @Path("getAgeGroupsStatisticsAsTimeSeries")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatisticsAsTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        DualSexStatisticsData data = getAgeGroupsStatisticsAsTimeSeriesData(request, filterHash);
        return Response.ok(data).build();
    }

    private DualSexStatisticsData getAgeGroupsStatisticsAsTimeSeriesData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getAgeGroupsStatisticsAsTimeSeries with filterHash: {}", filterHash);
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        KonDataResponse ageGroups = warehouse.getAldersgrupperSomTidsserie(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        return new SimpleMultiDualSexConverter().convert(ageGroups, range, filter);
    }

    /**
     * Get sjukfalls csv formatted.
     */
    @GET
    @Path("getAgeGroupsStatisticsAsTimeSeries/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatisticsAsTimeSeriesAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getAgeGroupsStatisticsAsTimeSeriesAsCsv with filterHash: {}", filterHash);
        final DualSexStatisticsData simpleDetailsData = getAgeGroupsStatisticsAsTimeSeriesData(request, filterHash);
        return CsvConverter.getCsvResponse(simpleDetailsData.getTableData(), "export.csv");
    }

    /**
     * Get sjukfall grouped by age and sex of the doctor.
     */
    @POST
    @Path("getCasesPerDoctorAgeAndGenderStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCasesPerDoctorAgeAndGenderStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        SimpleDetailsData data = getCasesPerDoctorAgeAndGenderStatisticsData(request, filterHash);
        return Response.ok(data).build();
    }

    private SimpleDetailsData getCasesPerDoctorAgeAndGenderStatisticsData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getCasesPerDoctorAgeAndGenderStatistics with filterHash: {}", filterHash);
        final Range range = new Range(12);
        Filter filter = getFilter(request, filterHash);
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getCasesPerDoctorAgeAndGender(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new DoctorAgeGenderConverter().convert(ageGroups, range, filter);
        return result;
    }

    /**
     * Get sjukfall grouped by age and sex of the doctor. Csv formatted.
     */
    @GET
    @Path("getCasesPerDoctorAgeAndGenderStatistics/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCasesPerDoctorAgeAndGenderStatisticsAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getCasesPerDoctorAgeAndGenderStatisticsAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData simpleDetailsData = getCasesPerDoctorAgeAndGenderStatisticsData(request, filterHash);
        return CsvConverter.getCsvResponse(simpleDetailsData.getTableData(), "export.csv");
    }

    /**
     * Get sjukfall grouped by doctor grade.
     */
    @POST
    @Path("getNumberOfCasesPerLakarbefattning")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakarbefattning(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        SimpleDetailsData data = getNumberOfCasesPerLakarbefattningData(request, filterHash);
        return Response.ok(data).build();
    }

    private SimpleDetailsData getNumberOfCasesPerLakarbefattningData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getNumberOfCasesPerLakarbefattning with filterHash: {}", filterHash);
        final Range range = new Range(12);
        Filter filter = getFilter(request, filterHash);
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getNumberOfCasesPerLakarbefattning(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new LakarbefattningConverter().convert(ageGroups, range, filter);
        return result;
    }

    /**
     * Get sjukfall grouped by doctor grade. Csv formatted.
     */
    @GET
    @Path("getNumberOfCasesPerLakarbefattning/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakarbefattningAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerLakarbefattningAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData simpleDetailsData = getNumberOfCasesPerLakarbefattningData(request, filterHash);
        return CsvConverter.getCsvResponse(simpleDetailsData.getTableData(), "export.csv");
    }

    /**
     * Get ongoing sjukfall grouped by age and sex.
     */
    @POST
    @Path("getAgeGroupsCurrentStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsCurrentStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        SimpleDetailsData data = getAgeGroupsCurrentStatisticsData(request, filterHash);
        return Response.ok(data).build();
    }

    private SimpleDetailsData getAgeGroupsCurrentStatisticsData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getAgeGroupsCurrentStatistics with filterHash: {}", filterHash);
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = new LocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);
        Filter filter = getFilter(request, filterHash);
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new AgeGroupsConverter().convert(ageGroups, range, filter);
        return result;
    }

    /**
     * Get ongoing sjukfall grouped by age and sex. Csv formatted.
     */
    @GET
    @Path("getAgeGroupsCurrentStatistics/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsCurrentStatisticsAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getAgeGroupsCurrentStatisticsAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData simpleDetailsData = getAgeGroupsCurrentStatisticsData(request, filterHash);
        return CsvConverter.getCsvResponse(simpleDetailsData.getTableData(), "export.csv");
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     */
    @POST
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        DualSexStatisticsData data = getDegreeOfSickLeaveStatisticsData(request, filterHash);
        return Response.ok(data).build();
    }

    private DualSexStatisticsData getDegreeOfSickLeaveStatisticsData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics with filterHash: {}", filterHash);
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        KonDataResponse degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradPerMonth(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, range, filter);
        return result;
    }

    /**
     * Get sjukskrivningsgrad per calendar month. Csv formatted.
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDegreeOfSickLeaveStatisticsAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDegreeOfSickLeaveStatisticsAsCsv with filterhash: {}", filterHash);
        final DualSexStatisticsData simpleDetailsData = getDegreeOfSickLeaveStatisticsData(request, filterHash);
        return CsvConverter.getCsvResponse(simpleDetailsData.getTableData(), "export.csv");
    }

    /**
     * Get sjukfallslangd (grouped).
     */
    @POST
    @Path("getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLengthData(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        SickLeaveLengthData data = getSickLeaveLengthDataData(request, filterHash);
        return Response.ok(data).build();
    }

    private SickLeaveLengthData getSickLeaveLengthDataData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getSickLeaveLengthData with filterHash: {}", filterHash);
        Range range = new Range(YEAR);
        Filter filter = getFilter(request, filterHash);
        SjukfallslangdResponse sickLeaveLength = warehouse.getSjukskrivningslangd(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SickLeaveLengthData result = new SickLeaveLengthConverter().convert(sickLeaveLength, range, filter);
        return result;
    }

    /**
     * Get sjukfallslangd (grouped). Csv formatted.
     */
    @GET
    @Path("getSickLeaveLengthData/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLengthDataAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getSickLeaveLengthDataAsCsv with filterHash: {}", filterHash);
        final SickLeaveLengthData simpleDetailsData = getSickLeaveLengthDataData(request, filterHash);
        return CsvConverter.getCsvResponse(simpleDetailsData.getTableData(), "export.csv");
    }

    /**
     * Get sjukfallslangd (grouped) for current month.
     */
    @POST
    @Path("getSickLeaveLengthCurrentData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLengthCurrentData(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        SickLeaveLengthData data = getSickLeaveLengthCurrentDataData(request, filterHash);
        return Response.ok(data).build();
    }

    private SickLeaveLengthData getSickLeaveLengthCurrentDataData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getSickLeaveLengthCurrentData with filterHash: {}", filterHash);
        LocalDate start = new LocalDate().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        final Range range = new Range(start, end);
        Filter filter = getFilter(request, filterHash);
        SjukfallslangdResponse sickLeaveLength = warehouse.getSjukskrivningslangd(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SickLeaveLengthData result = new SickLeaveLengthConverter().convert(sickLeaveLength, range, filter);
        return result;
    }

    /**
     * Get sjukfallslangd (grouped) for current month. Csv formatted.
     */
    @GET
    @Path("getSickLeaveLengthCurrentData/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLengthCurrentDataAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getSickLeaveLengthCurrentDataAsCsv with filterHash: {}", filterHash);
        final SickLeaveLengthData data = getSickLeaveLengthCurrentDataData(request, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    /**
     * Gets sjukfallslangd, grouped by sex, long / not long sjukfall.
     */
    @POST
    @Path("getLongSickLeavesData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getLongSickLeavesData(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        SimpleDetailsData data = getLongSickLeavesDataData(request, filterHash);
        return Response.ok(data).build();
    }

    private SimpleDetailsData getLongSickLeavesDataData(HttpServletRequest request, String filterHash) {
        LOG.info("Calling getLongSickLeavesData with filterHash: {}", filterHash);
        final Range range = new Range(18);
        Filter filter = getFilter(request, filterHash);
        SimpleKonResponse<SimpleKonDataRow> longSickLeaves = warehouse.getLangaSjukskrivningarPerManad(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new PeriodConverter().convert(longSickLeaves, range, filter);
        return result;
    }

    /**
     * Gets sjukfallslangd, grouped by sex, long / not long sjukfall. Csv formatted
     */
    @GET
    @Path("getLongSickLeavesData/csv")
    @Produces({ TEXT_UTF_8 })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getLongSickLeavesDataAsCsv(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getLongSickLeavesDataAsCsv with filterHash: {}", filterHash);
        final SimpleDetailsData data = getLongSickLeavesDataData(request, filterHash);
        return CsvConverter.getCsvResponse(data.getTableData(), "export.csv");
    }

    Filter getFilter(HttpServletRequest request, String filterHash) {
        if (filterHash == null || filterHash.isEmpty()) {
            return getFilterForAllAvailableEnhets(request);
        }
        FilterData inFilter = getFilterFromHash(filterHash);
        final ArrayList<String> enhetsIDs = getEnhetsFiltered(request, inFilter);
        Predicate<Fact> enhetFilter = getEnhetFilter(request, enhetsIDs);
        final List<String> diagnoser = inFilter.getDiagnoser();
        Predicate<Fact> diagnosFilter = getDiagnosFilter(diagnoser);
        final SjukfallFilter sjukfallFilter = new SjukfallFilter(Predicates.and(enhetFilter, diagnosFilter), filterHash);
        return new Filter(sjukfallFilter, enhetsIDs, diagnoser);
    }

    private Filter getFilterForAllAvailableEnhets(HttpServletRequest request) {
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        if (info.isProcessledare()) {
            return new Filter(SjukfallUtil.ALL_ENHETER, null, null);
        }
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(info.getBusinesses(), new Function<Verksamhet, Integer>() {
            @Override
            public Integer apply(Verksamhet verksamhet) {
                return Warehouse.getEnhet(verksamhet.getId());
            }
        }));
        return new Filter(new SjukfallFilter(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return availableEnhets.contains(fact.getEnhet());
            }
        }, SjukfallFilter.getHashValueForEnhets(availableEnhets.toArray())), null, null);
    }

    private ArrayList<String> getEnhetsFiltered(HttpServletRequest request, FilterData inFilter) {
        Set<String> enhetsMatchingVerksamhetstyp = getEnhetsForVerksamhetstyper(inFilter.getVerksamhetstyper(), request);
        final HashSet<String> enhets = new HashSet<>(inFilter.getEnheter());
        return new ArrayList<>(Sets.intersection(enhetsMatchingVerksamhetstyp, enhets));
    }

    private Set<String> getEnhetsForVerksamhetstyper(List<String> verksamhetstyper, HttpServletRequest request) {
        Set<String> enhetsIds = new HashSet<>();
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        for (Verksamhet verksamhet : info.getBusinesses()) {
            if (isOfVerksamhetsTyp(verksamhet, verksamhetstyper)) {
                enhetsIds.add(verksamhet.getId());
            }
        }
        return enhetsIds;
    }

    private boolean isOfVerksamhetsTyp(Verksamhet verksamhet, List<String> verksamhetstyper) {
        final Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet = verksamhet.getVerksamhetsTyper();
        if (verksamhetstyperForCurrentVerksamhet == null || verksamhetstyperForCurrentVerksamhet.isEmpty()) {
            return true;
        }
        for (Verksamhet.VerksamhetsTyp verksamhetsTyp : verksamhetstyperForCurrentVerksamhet) {
            if (verksamhetstyper.contains(verksamhetsTyp.getId())) {
                return true;
            }
        }
        return false;
    }

    private FilterData getFilterFromHash(String filterHash) {
        final Optional<String> filterData = filterHashHandler.getFilterData(filterHash);
        if (!filterData.isPresent()) {
            throw new RuntimeException("Could not find filter with given hash");
        }
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(filterData.get(), FilterData.class);
        } catch (IOException e) {
            LOG.error("Failed to get filter data from hash: " + filterHash + ". Data: " + filterData.get(), e);
            throw new RuntimeException("Filter data failed");
        }
    }

    private Predicate<Fact> getDiagnosFilter(final List<String> diagnosIds) {
        return new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                if (diagnosIds == null || diagnosIds.isEmpty()) {
                    return false;
                }
                String diagnosKapitelString = String.valueOf(fact.getDiagnoskapitel());
                if (diagnosIds.contains(diagnosKapitelString)) {
                    return true;
                }
                String diagnosAvsnittString = String.valueOf(fact.getDiagnosavsnitt());
                if (diagnosIds.contains(diagnosAvsnittString)) {
                    return true;
                }
                String diagnosKategoriString = String.valueOf(fact.getDiagnoskategori());
                if (diagnosIds.contains(diagnosKategoriString)) {
                    return true;
                }
                return false;
            }
        };
    }

    Predicate<Fact> getEnhetFilter(HttpServletRequest request, List<String> enhetsIDs) {
        Set<String> enheter = getEnhetNameMap(request, enhetsIDs).keySet();
        return sjukfallUtil.createEnhetFilter(enheter.toArray(new String[enheter.size()])).getFilter();
    }

    private Map<String, String> getEnhetNameMap(HttpServletRequest request, List<String> enhetsIDs) {
        final String vgid = getSelectedVgIdForLoggedInUser(request);
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        Map<String, String> enheter = new HashMap<>();
        for (Verksamhet userVerksamhet : info.getBusinesses()) {
            if (userVerksamhet.getVardgivarId().equals(vgid)) {
                if (enhetsIDs != null && enhetsIDs.contains(userVerksamhet.getId())) {
                    enheter.put(userVerksamhet.getId(), userVerksamhet.getName());
                }
            }
        }
        return enheter;
    }

    private String getSelectedVgIdForLoggedInUser(HttpServletRequest request) {
        return loginServiceUtil.getLoginInfo(request).getDefaultVerksamhet().getVardgivarId();
    }

    public boolean hasAccessTo(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return loginServiceUtil.getLoginInfo(request).isLoggedIn();
    }

    public boolean userAccess(HttpServletRequest request) {
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo(request);
        LOG.info("User " + loginInfo.getHsaId() + " accessed verksamhet " + loginInfo.getDefaultVerksamhet().getVardgivarId() + " (" + getUriSafe(request) + ") session " + request.getSession().getId());
        return true;
    }

    private String getUriSafe(HttpServletRequest request) {
        if (request == null) {
            return "!NoRequest!";
        }
        return request.getRequestURI();
    }

}
