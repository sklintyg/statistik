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
import se.inera.statistics.service.report.api.RollingLength;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.service.warehouse.query.CalcException;
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

    private static final String VERKSAMHET_PATH_ID = "verksamhetId";
    private static final Logger LOG = LoggerFactory.getLogger(ProtectedChartDataService.class);
    private static final String TEXT_CP1252 = ChartDataService.TEXT_CP1252;

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
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerMonth(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerMonth with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        Optional<SimpleDetailsData> result = getNumberOfCasesPerMonthData(request, verksamhetId, filterHash);
        if (result.isPresent()) {
            return Response.ok(result.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getNumberOfCasesPerMonthData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getNumberOfCasesPerMonth with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final Range range = new Range(18);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonth(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new PeriodConverter().convert(casesPerMonth, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerMonthAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerMonthAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> data = getNumberOfCasesPerMonthData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Gets sjukfall per enhet for verksamhetId.
     */
    @POST
    @Path("{verksamhetId}/getNumberOfCasesPerEnhet")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerEnhet(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerEnhet with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        Optional<SimpleDetailsData> result = getNumberOfCasesPerEnhetData(request, verksamhetId, filterHash);
        if (result.isPresent()) {
            return Response.ok(result.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getNumberOfCasesPerEnhetData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getNumberOfCasesPerEnhet with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final Range range = new Range(12);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            Map<String, String> idToNameMap = getEnhetNameMap(request, verksamhet, getEnhetsFilterIds(filterHash, request));
            SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerEnhet(filter.getPredicate(), idToNameMap, range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new GroupedSjukfallConverter("Vårdenhet").convert(casesPerEnhet, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

    /**
     * Gets sjukfall per enhet for verksamhetId, csv formatted.
     */
    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerEnhet/csv")
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerEnhetAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerEnhetAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> data = getNumberOfCasesPerEnhetData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
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
    @Path("{verksamhetId}/getNumberOfCasesPerLakare")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerLakare(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerLakare with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> result = getNumberOfCasesPerLakareData(request, verksamhetId, filterHash);
        if (result.isPresent()) {
            return Response.ok(result.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getNumberOfCasesPerLakareData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            final Range range = new Range(12);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SimpleKonResponse<SimpleKonDataRow> casesPerLakare = warehouse.getCasesPerLakare(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new GroupedSjukfallConverter("Läkare").convert(casesPerLakare, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

    /**
     * Gets sjukfall per doctor for verksamhetId. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getSjukfallPerLakareVerksamhet/csv")
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerLakareAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerLakareAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> data = getNumberOfCasesPerLakareData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get sjukfall per diagnoskapitel and per diagnosgrupp. The chart data is grouped by diagnosgrupp,
     * the table data by diagnoskapitel. Diagnosgrupp is a diagnoskapitel or a list of diagnoskapitel.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getDiagnoskapitelstatistik")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisGroupStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnoskapitelstatistik with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        Optional<DualSexStatisticsData> result = getDiagnosisGroupStatisticsData(request, verksamhetId, filterHash);
        if (result.isPresent()) {
            return Response.ok(result.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<DualSexStatisticsData> getDiagnosisGroupStatisticsData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            final Range range = new Range(18);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            DiagnosgruppResponse diagnosisGroups = warehouse.getDiagnosgrupperPerMonth(filter.getPredicate(), range, verksamhet.getVardgivarId());
            DualSexStatisticsData result = new DiagnosisGroupsConverter().convert(diagnosisGroups, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnoskapitelstatistikAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<DualSexStatisticsData> data = getDiagnosisGroupStatisticsData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getDiagnosavsnittstatistik/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @PathParam("groupId") String groupId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnosavsnittstatistik with verksamhetId: {} and groupId: {} and filterHash: {}", verksamhetId, groupId, filterHash);
        try {
            Optional<DualSexStatisticsData> data = getDiagnosisSubGroupStatisticsEntity(request, verksamhetId, groupId, filterHash);
            if (data.isPresent()) {
                return Response.ok(data.get()).build();
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private Optional<DualSexStatisticsData> getDiagnosisSubGroupStatisticsEntity(HttpServletRequest request, String verksamhetId, String groupId, String filterHash) throws RangeNotFoundException {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            final Range range = new Range(18);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            final String vardgivarId = verksamhet.getVardgivarId();
            DiagnosgruppResponse diagnosavsnitt = warehouse.getUnderdiagnosgrupper(filter.getPredicate(), range, groupId, vardgivarId);
            final String message = getDiagnosisSubGroupStatisticsMessage(filter, Arrays.asList(String.valueOf(icd10.findFromIcd10Code(groupId).toInt())));
            DualSexStatisticsData result = new DiagnosisSubGroupsConverter().convert(diagnosavsnitt, range, filter, message);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

    private String getDiagnosisSubGroupStatisticsMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {
            return "Du har gjort ett val av diagnoskapitel eller diagnosavsnitt som inte matchar det val du gjort i diagnosfilter (se Visa filter högst upp på sidan).";
        }
        return null;
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDiagnosisSubGroupStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @PathParam("groupId") String groupId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDiagnosavsnittstatistikAsCsv with verksamhetId: {} and groupId: {} and filterHash: {}", verksamhetId, groupId, filterHash);
        final Optional<DualSexStatisticsData> data;
        try {
            data = getDiagnosisSubGroupStatisticsEntity(request, verksamhetId, groupId, filterHash);
            if (data.isPresent()) {
                return CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv");
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("{verksamhetId}/getJamforDiagnoserStatistik/{diagnosHash}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getCompareDiagnosisStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @PathParam("diagnosHash") String diagnosisHash, @QueryParam("filter") String filterHash) {
        Optional<SimpleDetailsData> data = getCompareDiagnosisStatisticsData(request, verksamhetId, diagnosisHash, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getCompareDiagnosisStatisticsData(HttpServletRequest request, String verksamhetId, String diagnosisHash, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getCompareDiagnosisStatistics with verksamhetId: {} and diagnosis: {} and filterHash: {}", verksamhetId, diagnosisHash, filterHash);
            final Range range = new Range(12);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
            final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String>emptyList() : getFilterFromHash(diagnosisHash).getDiagnoser();
            final String message = emptyDiagnosisHash ? "Inga diagnoser valda" : getCompareDiagnosisMessage(filter, diagnosis);
            SimpleKonResponse<SimpleKonDataRow> resultRows = warehouse.getJamforDiagnoser(filter.getPredicate(), range, verksamhet.getVardgivarId(), diagnosis);
            SimpleDetailsData result = new CompareDiagnosisConverter().convert(resultRows, range, filter, message);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

    @GET
    @Path("{verksamhetId}/getJamforDiagnoserStatistik/{diagnosHash}/csv")
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getCompareDiagnosisStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash, @PathParam("diagnosHash") String diagnosisHash) {
        LOG.info("Calling getCompareDiagnosisStatisticsAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> data = getCompareDiagnosisStatisticsData(request, verksamhetId, diagnosisHash, filterHash);
        if (data.isPresent()) {
            return Response.ok(CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv")).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private String getCompareDiagnosisMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {
            return "Du har gjort ett val av diagnos som inte matchar det val du gjort i diagnosfilter (se Visa filter högst upp på sidan).";
        }
        return null;
    }

    /**
     * Get overview. Includes total n:o of sjukfall, sex distribution, top lists for diagnosgrupp, aldersgrupp, sjukskrivningslangd,
     * sjukskrivningsgrad. Only chart formatted data.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getOverviewData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getOverview with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);

            final Range range = Range.quarter();
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            VerksamhetOverviewResponse response = warehouse.getOverview(filter.getPredicate(), range, verksamhet.getVardgivarId());
            return Response.ok(new VerksamhetOverviewConverter().convert(response, range, filter)).build();
        } catch (CalcException c) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

    /**
     * Get sjukfall grouped by age and sex.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<SimpleDetailsData> data = getAgeGroupsStatisticsData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getAgeGroupsStatisticsData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getAgeGroupsStatistics with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final Range range = new Range(12);

            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new AgeGroupsConverter().convert(ageGroups, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getAgeGroupsStatisticsAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> simpleDetailsData = getAgeGroupsStatisticsData(request, verksamhetId, filterHash);
        if (simpleDetailsData.isPresent()) {
            return CsvConverter.getCsvResponse(simpleDetailsData.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get sjukfall grouped by age and sex of the doctor.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getCasesPerDoctorAgeAndGenderStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getCasesPerDoctorAgeAndGenderStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<SimpleDetailsData> data = getCasesPerDoctorAgeAndGenderStatisticsData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getCasesPerDoctorAgeAndGenderStatisticsData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getCasesPerDoctorAgeAndGenderStatistics with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final Range range = new Range(12);

            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getCasesPerDoctorAgeAndGender(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new DoctorAgeGenderConverter().convert(ageGroups, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

    /**
     * Get sjukfall grouped by age and sex of the doctor. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getCasesPerDoctorAgeAndGenderStatistics/csv")
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getCasesPerDoctorAgeAndGenderStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getCasesPerDoctorAgeAndGenderStatisticsAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> simpleDetailsData = getCasesPerDoctorAgeAndGenderStatisticsData(request, verksamhetId, filterHash);
        if (simpleDetailsData.isPresent()) {
            return CsvConverter.getCsvResponse(simpleDetailsData.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get sjukfall grouped by doctor grade.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getNumberOfCasesPerLakarbefattning")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerLakarbefattning(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<SimpleDetailsData> data = getNumberOfCasesPerLakarbefattningData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getNumberOfCasesPerLakarbefattningData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getNumberOfCasesPerLakarbefattning with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final Range range = new Range(12);

            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getNumberOfCasesPerLakarbefattning(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new LakarbefattningConverter().convert(ageGroups, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

    /**
     * Get sjukfall grouped by doctor grade. Csv formatted.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @GET
    @Path("{verksamhetId}/getNumberOfCasesPerLakarbefattning/csv")
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getNumberOfCasesPerLakarbefattningAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getNumberOfCasesPerLakarbefattningAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> simpleDetailsData = getNumberOfCasesPerLakarbefattningData(request, verksamhetId, filterHash);
        if (simpleDetailsData.isPresent()) {
            return CsvConverter.getCsvResponse(simpleDetailsData.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get ongoing sjukfall grouped by age and sex.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getAgeGroupsCurrentStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsCurrentStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<SimpleDetailsData> data = getAgeGroupsCurrentStatisticsData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getAgeGroupsCurrentStatisticsData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getAgeGroupsCurrentStatistics with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            LocalDate start = new LocalDate().withDayOfMonth(1);
            LocalDate end = new LocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1);
            final Range range = new Range(start, end);

            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new AgeGroupsConverter().convert(ageGroups, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getAgeGroupsCurrentStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getAgeGroupsCurrentStatisticsAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> simpleDetailsData = getAgeGroupsCurrentStatisticsData(request, verksamhetId, filterHash);
        if (simpleDetailsData.isPresent()) {
            return CsvConverter.getCsvResponse(simpleDetailsData.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<DualSexStatisticsData> data = getDegreeOfSickLeaveStatisticsData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<DualSexStatisticsData> getDegreeOfSickLeaveStatisticsData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getDegreeOfSickLeaveStatistics with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final Range range = new Range(18);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SjukskrivningsgradResponse degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradPerMonth(filter.getPredicate(), range, verksamhet.getVardgivarId());
            DualSexStatisticsData result = new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getDegreeOfSickLeaveStatisticsAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getDegreeOfSickLeaveStatisticsAsCsv with verksamhetId: {} and filterhash: {}", verksamhetId, filterHash);
        final Optional<DualSexStatisticsData> simpleDetailsData = getDegreeOfSickLeaveStatisticsData(request, verksamhetId, filterHash);
        if (simpleDetailsData.isPresent()) {
            return CsvConverter.getCsvResponse(simpleDetailsData.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get sjukfallslangd (grouped).
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<SickLeaveLengthData> data = getSickLeaveLengthDataData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SickLeaveLengthData> getSickLeaveLengthDataData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getSickLeaveLengthData with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final RollingLength year = RollingLength.YEAR;
            Range range = new Range(year.getPeriods());
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SjukfallslangdResponse sickLeaveLength = warehouse.getSjukskrivningslangd(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SickLeaveLengthData result = new SickLeaveLengthConverter().convert(sickLeaveLength, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getSickLeaveLengthDataAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SickLeaveLengthData> simpleDetailsData = getSickLeaveLengthDataData(request, verksamhetId, filterHash);
        if (simpleDetailsData.isPresent()) {
            return CsvConverter.getCsvResponse(simpleDetailsData.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Get sjukfallslangd (grouped) for current month.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getSickLeaveLengthCurrentData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthCurrentData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<SickLeaveLengthData> data = getSickLeaveLengthCurrentDataData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SickLeaveLengthData> getSickLeaveLengthCurrentDataData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getSickLeaveLengthCurrentData with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            LocalDate start = new LocalDate().withDayOfMonth(1);
            LocalDate end = start.plusMonths(1).minusDays(1);
            final Range range = new Range(start, end);
            Filter filter = getFilter(request, verksamhet, filterHash);
            SjukfallslangdResponse sickLeaveLength = warehouse.getSjukskrivningslangd(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SickLeaveLengthData result = new SickLeaveLengthConverter().convert(sickLeaveLength, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getSickLeaveLengthCurrentDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getSickLeaveLengthCurrentDataAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SickLeaveLengthData> data = getSickLeaveLengthCurrentDataData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /**
     * Gets sjukfallslangd, grouped by sex, long / not long sjukfall.
     *
     * @param request      request
     * @param verksamhetId verksamhetId
     * @return data
     */
    @POST
    @Path("{verksamhetId}/getLongSickLeavesData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getLongSickLeavesData(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        Optional<SimpleDetailsData> data = getLongSickLeavesDataData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return Response.ok(data.get()).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    private Optional<SimpleDetailsData> getLongSickLeavesDataData(HttpServletRequest request, String verksamhetId, String filterHash) {
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            LOG.info("Calling getLongSickLeavesData with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
            final Range range = new Range(18);
            Verksamhet verksamhet = getVerksamhet(request, Verksamhet.decodeId(verksamhetId));
            Filter filter = getFilter(request, verksamhet, filterHash);
            SimpleKonResponse<SimpleKonDataRow> longSickLeaves = warehouse.getLangaSjukskrivningarPerManad(filter.getPredicate(), range, verksamhet.getVardgivarId());
            SimpleDetailsData result = new PeriodConverter().convert(longSickLeaves, range, filter);
            return Optional.of(result);
        } catch (CalcException c) {
            return Optional.absent();
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
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
    @Produces({TEXT_CP1252})
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request, #verksamhetId)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request, #verksamhetId)")
    public Response getLongSickLeavesDataAsCsv(@Context HttpServletRequest request, @PathParam(VERKSAMHET_PATH_ID) String verksamhetId, @QueryParam("filter") String filterHash) {
        LOG.info("Calling getLongSickLeavesDataAsCsv with verksamhetId: {} and filterHash: {}", verksamhetId, filterHash);
        final Optional<SimpleDetailsData> data = getLongSickLeavesDataData(request, verksamhetId, filterHash);
        if (data.isPresent()) {
            return CsvConverter.getCsvResponse(data.get().getTableData(), "export.csv");
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }

    Filter getFilter(HttpServletRequest request, Verksamhet verksamhet, String filterHash) {
        if (filterHash == null || filterHash.isEmpty()) {
            return getFilterForAllAvailableEnhets(request);
        }
        FilterData inFilter = getFilterFromHash(filterHash);
        final ArrayList<String> enhetsIDs = getEnhetsFiltered(request, inFilter);
        Predicate<Fact> enhetFilter = getEnhetFilter(request, verksamhet, enhetsIDs);
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

    Predicate<Fact> getEnhetFilter(HttpServletRequest request, Verksamhet
            verksamhet, List<String> enhetsIDs) {
        Set<String> enheter = getEnhetNameMap(request, verksamhet, enhetsIDs).keySet();
        return sjukfallUtil.createEnhetFilter(enheter.toArray(new String[enheter.size()])).getFilter();
    }

    private Map<String, String> getEnhetNameMap(HttpServletRequest request, Verksamhet
            verksamhet, List<String> enhetsIDs) {

        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        Map<String, String> enheter = new HashMap<>();
        for (Verksamhet userVerksamhet : info.getBusinesses()) {
            if (userVerksamhet.getVardgivarId().equals(verksamhet.getVardgivarId())) {
                if (enhetsIDs != null && enhetsIDs.contains(userVerksamhet.getId())) {
                    enheter.put(userVerksamhet.getId(), userVerksamhet.getName());
                }
            }
        }
        return enheter;
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
