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
import com.sun.istack.NotNull;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import se.inera.statistics.service.landsting.LandstingEnhetFileData;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.NoLandstingSetForVgException;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdate;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateOperation;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.service.landsting.LandstingEnhetFileParseException;
import se.inera.statistics.web.service.landsting.LandstingFileReader;

import javax.activation.DataSource;
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

    @Autowired
    private LandstingEnhetHandler landstingEnhetHandler;

    @Autowired
    private EnhetManager enhetManager;

    private LandstingFileReader landstingFileReader = new LandstingFileReader();

    /**
     * Gets sjukfall per manad for verksamhetId.
     */
    @GET
    @Path("getNumberOfCasesPerMonth{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerMonth(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonth(filterSettings.getFilter().getPredicate(), filterSettings.getRange(), getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new PeriodConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, csv);
    }

    private Response getResponse(TableDataReport result, String csv) {
        if (csv == null || csv.isEmpty()) {
            return Response.ok(result).build();
        }
        return CsvConverter.getCsvResponse(result.getTableData(), "export.csv");
    }

    @GET
    @Path("getNumberOfCasesPerMonthTvarsnitt{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerMonthTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonthTvarsnitt(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new TotalCasesPerMonthTvarsnittConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, csv);
    }

    /**
     * Gets sjukfall per enhet for verksamhetId.
     */
    @GET
    @Path("getNumberOfCasesPerEnhet{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhet(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        Map<String, String> idToNameMap = getEnhetNameMap(request, getEnhetsFilterIds(filterHash, request));
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerEnhet(filter.getPredicate(), idToNameMap, range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new GroupedSjukfallConverter("Vårdenhet").convert(casesPerEnhet, filterSettings);
        return getResponse(result, csv);
    }

    /**
     * Gets sjukfall per enhet for verksamhetId i tidsserie.
     */
    @GET
    @Path("getNumberOfCasesPerEnhetTimeSeries{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhetTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        Map<String, String> idToNameMap = getEnhetNameMap(request, getEnhetsFilterIds(filterHash, request));
        KonDataResponse casesPerEnhet = warehouse.getCasesPerEnhetTimeSeries(filter.getPredicate(), idToNameMap, range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new SimpleMultiDualSexConverter().convert(casesPerEnhet, filterSettings);
        return getResponse(result, csv);
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
    @GET
    @Path("getNumberOfCasesPerLakare{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakare(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> casesPerLakare = warehouse.getCasesPerLakare(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        final SimpleDetailsData result = new GroupedSjukfallConverter("Läkare").convert(casesPerLakare, filterSettings);
        return getResponse(result, csv);
    }

    @GET
    @Path("getSjukfallPerLakareSomTidsserie{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakareSomTidsserie(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse casesPerLakare = warehouse.getCasesPerLakareSomTidsserie(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        final DualSexStatisticsData result = new SimpleMultiDualSexConverter().convert(casesPerLakare, filterSettings);
        return getResponse(result, csv);
    }

    /**
     * Get sjukfall per diagnoskapitel and per diagnosgrupp. The chart data is grouped by diagnosgrupp,
     * the table data by diagnoskapitel. Diagnosgrupp is a diagnoskapitel or a list of diagnoskapitel.
     */
    @GET
    @Path("getDiagnoskapitelstatistik{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisGroupStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        DiagnosgruppResponse diagnosisGroups = warehouse.getDiagnosgrupperPerMonth(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new DiagnosisGroupsConverter().convert(diagnosisGroups, filterSettings);
        return getResponse(result, csv);
    }

    @GET
    @Path("getDiagnosGruppTvarsnitt{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisGroupTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> diagnosisGroups = warehouse.getDiagnosgrupperTvarsnitt(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new DiagnosisGroupsTvarsnittConverter().convert(diagnosisGroups, filterSettings);
        return getResponse(result, csv);
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam("groupId") String groupId, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        try {
            final FilterSettings filterSettings = getFilter(request, filterHash, 18);
            final Filter filter = filterSettings.getFilter();
            final Range range = filterSettings.getRange();
            final DiagnosgruppResponse diagnosavsnitt = warehouse.getUnderdiagnosgrupper(filter.getPredicate(), range, groupId, getSelectedVgIdForLoggedInUser(request));
            final String message = getDiagnosisSubGroupStatisticsMessage(filter, Arrays.asList(String.valueOf(icd10.findFromIcd10Code(groupId).toInt())));
            final DualSexStatisticsData data = new DiagnosisSubGroupsConverter().convert(diagnosavsnitt, filterSettings, message);
            return getResponse(data, csv);
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private String getDiagnosisSubGroupStatisticsMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {
            return "Du har gjort ett val av diagnoskapitel eller diagnosavsnitt som inte matchar det val du gjort i diagnosfilter (se Visa filter högst upp på sidan).";
        }
        return null;
    }

    @GET
    @Path("getDiagnosavsnittTvarsnitt/{groupId}{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisSubGroupTvarsnitt(@Context HttpServletRequest request, @PathParam("groupId") String groupId, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        try {
            final FilterSettings filterSettings = getFilter(request, filterHash, 12);
            final Filter filter = filterSettings.getFilter();
            final Range range = filterSettings.getRange();
            final SimpleKonResponse<SimpleKonDataRow> diagnosavsnitt = warehouse.getUnderdiagnosgrupperTvarsnitt(filter.getPredicate(), range, groupId, getSelectedVgIdForLoggedInUser(request));
            final String message = getDiagnosisSubGroupStatisticsMessage(filter, Arrays.asList(String.valueOf(icd10.findFromIcd10Code(groupId).toInt())));
            final SimpleDetailsData data = new DiagnosisSubGroupsTvarsnittConverter().convert(diagnosavsnitt, filterSettings, message);
            return getResponse(data, csv);
        } catch (RangeNotFoundException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getJamforDiagnoserStatistik/{diagnosHash}{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatistics(@Context HttpServletRequest request, @PathParam("diagnosHash") String diagnosisHash, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String>emptyList() : getFilterFromHash(diagnosisHash).getDiagnoser();
        final String message = emptyDiagnosisHash ? "Inga diagnoser valda" : getCompareDiagnosisMessage(filter, diagnosis);
        SimpleKonResponse<SimpleKonDataRow> resultRows = warehouse.getJamforDiagnoser(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request), diagnosis);
        SimpleDetailsData result = new CompareDiagnosisConverter().convert(resultRows, filterSettings, message);
        SimpleDetailsData data = result;
        return getResponse(data, csv);
    }

    private String getCompareDiagnosisMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {
            return "Du har gjort ett val av diagnos som inte matchar det val du gjort i diagnosfilter (se Visa filter högst upp på sidan).";
        }
        return null;
    }

    @GET
    @Path("getJamforDiagnoserStatistikTidsserie/{diagnosHash}{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatisticsTimeSeries(@Context HttpServletRequest request, @PathParam("diagnosHash") String diagnosisHash, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String>emptyList() : getFilterFromHash(diagnosisHash).getDiagnoser();
        final String message = emptyDiagnosisHash ? "Inga diagnoser valda" : getCompareDiagnosisMessage(filter, diagnosis);
        KonDataResponse resultRows = warehouse.getJamforDiagnoserTidsserie(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request), diagnosis);
        DualSexStatisticsData data = new CompareDiagnosisTimeSeriesConverter().convert(resultRows, filterSettings, message);
        return getResponse(data, csv);
    }

    /**
     * Get overview. Includes total n:o of sjukfall, sex distribution, top lists for diagnosgrupp, aldersgrupp, sjukskrivningslangd,
     * sjukskrivningsgrad. Only chart formatted data.
     */
    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getOverviewData(@Context HttpServletRequest request, @QueryParam("filter") String filterHash) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 3);
        final Filter filter = filterSettings.getFilter();
        final Range range = Range.quarter();
        final String message = filterHash == null || filterHash.isEmpty() || getFilterFromHash(filterHash).isUseDefaultPeriod() ? null : "Valt tidsintervall i filtret gäller inte för översiktssidan";
        VerksamhetOverviewResponse response = warehouse.getOverview(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        return Response.ok(new VerksamhetOverviewConverter().convert(response, range, filter, message)).build();
    }

    /**
     * Get sjukfall grouped by age and sex.
     */
    @GET
    @Path("getAgeGroupsStatistics{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new AgeGroupsConverter().convert(ageGroups, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getAgeGroupsStatisticsAsTimeSeries{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatisticsAsTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getAldersgrupperSomTidsserie(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(ageGroups, filterSettings);
        return getResponse(data, csv);
    }

    /**
     * Get sjukfall grouped by age and sex of the doctor.
     */
    @GET
    @Path("getCasesPerDoctorAgeAndGenderStatistics{csv:(/csv)?}")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCasesPerDoctorAgeAndGenderStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getCasesPerDoctorAgeAndGender(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new DoctorAgeGenderConverter().convert(ageGroups, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getCasesPerDoctorAgeAndGenderTimeSeriesStatistics{csv:(/csv)?}")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCasesPerDoctorAgeAndGenderTimeSeriesStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getCasesPerDoctorAgeAndGenderTimeSeries(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(ageGroups, filterSettings);
        return getResponse(data, csv);
    }

    /**
     * Get sjukfall grouped by doctor grade.
     */
    @GET
    @Path("getNumberOfCasesPerLakarbefattning{csv:(/csv)?}")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakarbefattning(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getNumberOfCasesPerLakarbefattning(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new LakarbefattningConverter().convert(ageGroups, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getNumberOfCasesPerLakarbefattningSomTidsserie{csv:(/csv)?}")
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakarbefattningSomTidsserie(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getNumberOfCasesPerLakarbefattningSomTidsserie(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(ageGroups, filterSettings);
        return getResponse(data, csv);
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradPerMonth(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getDegreeOfSickLeaveTvarsnitt{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDegreeOfSickLeaveTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradTvarsnitt(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new SimpleDualSexConverter("", false, "%1$s%% sjukskrivningsgrad").convert(degreeOfSickLeaveStatistics, filterSettings);
        return getResponse(data, csv);
    }

    /**
     * Get sjukfallslangd (grouped).
     */
    @GET
    @Path("getSickLeaveLengthData{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLength(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> sickLeaveLength = warehouse.getSjukskrivningslangd(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new SickLeaveLengthConverter().convert(sickLeaveLength, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getSickLeaveLengthTimeSeries{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLengthTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse sickLeaveLength = warehouse.getSjukskrivningslangdTidsserie(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(sickLeaveLength, filterSettings);
        return getResponse(data, csv);
    }

    /**
     * Gets sjukfallslangd, grouped by sex, long / not long sjukfall.
     */
    @GET
    @Path("getLongSickLeavesData{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getLongSickLeavesData(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> longSickLeaves = warehouse.getLangaSjukskrivningarPerManad(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new PeriodConverter().convert(longSickLeaves, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getLongSickLeavesTvarsnitt{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getLongSickLeavesTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> longSickLeaves = warehouse.getLangaSjukskrivningarTvarsnitt(filter.getPredicate(), range, getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new LongSickLeaveTvarsnittConverter().convert(longSickLeaves, filterSettings);
        return getResponse(data, csv);
    }

    @POST
    @Path("landsting/fileupload")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ "multipart/form-data" })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response fileupload(@Context HttpServletRequest request, MultipartBody body) {
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        if (!info.isProcessledare()) {
            LOG.warn("A user without processledar status tried to updated landstingsdata");
            return createFileUploadResponse(Response.Status.FORBIDDEN, "Data NOT updated", null);
        }
        final DataSource dataSource = body.getAttachment("file").getDataHandler().getDataSource();
        try {
            final List<LandstingEnhetFileDataRow> landstingFileRows = landstingFileReader.readExcelData(dataSource);
            final String vardgivarId = info.getDefaultVerksamhet().getVardgivarId();
            final LandstingEnhetFileData fileData = new LandstingEnhetFileData(vardgivarId, landstingFileRows, info.getName(), info.getHsaId(), dataSource.getName());
            landstingEnhetHandler.update(fileData);
            return createFileUploadResponse(Response.Status.OK, "Data updated ok", landstingFileRows);
        } catch (LandstingEnhetFileParseException e) {
            LOG.warn("Failed to parse landstings file", e);
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, "Data NOT updated: " + e.getMessage(), null);
        } catch (NoLandstingSetForVgException e) {
            LOG.warn("Failed to update landsting settings", e);
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, "Data NOT updated: Current vårdgivare is not connected to a landsting", null);
        }
    }

    @GET
    @Path("landsting/lastUpdateInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ "multipart/form-data" })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getLastLandstingUpdateInfo(@Context HttpServletRequest request) {
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        final String vardgivarId = info.getDefaultVerksamhet().getVardgivarId();
        return Response.ok(getLastLandstingUpdateInfoMessage(vardgivarId)).build();
    }

    private String getLastLandstingUpdateInfoMessage(String vardgivarId) {
        final Optional<LandstingEnhetUpdate> lastUpdateInfo = landstingEnhetHandler.getLastUpdateInfo(vardgivarId);
        if (lastUpdateInfo.isPresent()) {
            final LandstingEnhetUpdate update = lastUpdateInfo.get();
            final LandstingEnhetUpdateOperation operation = update.getOperation();
            String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(update.getTimestamp().getTime()));
            return operation.getMessage() + (LandstingEnhetUpdateOperation.Update.equals(operation) ? " (" + update.getFilename() + ")" : "") + " - " + dateTime + " av " + update.getUpdatedByName() + " (" + update.getUpdatedByHsaid() + ")";
        }
        return "Finns ej...";
    }

    private Response createFileUploadResponse(Response.Status status, String message, List<LandstingEnhetFileDataRow> landstingFileRows) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        final List<String> parsedRowsStrings = landstingFileRows == null ? null : Lists.transform(landstingFileRows, new Function<LandstingEnhetFileDataRow, String>() {
            @Override
            public String apply(LandstingEnhetFileDataRow landstingEnhetFileDataRow) {
                return "HSA-id: " + landstingEnhetFileDataRow.getEnhetensHsaId() + " -> Listade patienter: " + landstingEnhetFileDataRow.getListadePatienter();
            }
        });
        map.put("parsedRows", parsedRowsStrings);
        return Response.status(status).entity(map).build();
    }

    @GET
    @Path("landsting/getNumberOfCasesPerMonthLandsting{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerMonthLandsting(@Context HttpServletRequest request, @QueryParam("landstingfilter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = getFilterForLandsting(request, filterHash, 18);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonthLandsting(filterSettings);
        SimpleDetailsData result = new PeriodConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, csv);
    }

    FilterSettings getFilterForLandsting(HttpServletRequest request, String filterHash, int defaultRangeValue) {
        if (filterHash == null || filterHash.isEmpty()) {
            return new FilterSettings(getFilterForAllAvailableEnhetsLandsting(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue));
        }
        final FilterData inFilter = getFilterFromHash(filterHash);
        final ArrayList<String> enhetsIDs = getEnhetsFilteredLandsting(request, inFilter);
        try {
            return getFilterSettings(request, filterHash, defaultRangeValue, inFilter, enhetsIDs);
        } catch (FilterException e) {
            LOG.warn("Could not use selected filter. Falling back to default filter", e);
            return new FilterSettings(getFilterForAllAvailableEnhetsLandsting(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue), "Kunde ej applicera valt filter. Vänligen kontrollera filterinställningarna.");
        }
    }

    private FilterSettings getFilterSettings(HttpServletRequest request, String filterHash, int defaultRangeValue, FilterData inFilter, ArrayList<String> enhetsIDs) throws FilterException {
        final Predicate<Fact> enhetFilter = getEnhetFilter(request, enhetsIDs);
        final List<String> diagnoser = inFilter.getDiagnoser();
        final Predicate<Fact> diagnosFilter = getDiagnosFilter(diagnoser);
        final SjukfallFilter sjukfallFilter = new SjukfallFilter(Predicates.and(enhetFilter, diagnosFilter), filterHash);
        final Filter filter = new Filter(sjukfallFilter, enhetsIDs, diagnoser);
        final Range range = getRange(inFilter, defaultRangeValue);
        return new FilterSettings(filter, range);
    }

    FilterSettings getFilter(HttpServletRequest request, String filterHash, int defaultRangeValue) {
        if (filterHash == null || filterHash.isEmpty()) {
            return new FilterSettings(getFilterForAllAvailableEnhets(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue));
        }
        final FilterData inFilter = getFilterFromHash(filterHash);
        final ArrayList<String> enhetsIDs = getEnhetsFiltered(request, inFilter);
        try {
            return getFilterSettings(request, filterHash, defaultRangeValue, inFilter, enhetsIDs);
        } catch (FilterException e) {
            LOG.warn("Could not use selected filter. Falling back to default filter", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue), "Kunde ej applicera valt filter. Vänligen kontrollera filterinställningarna.");
        }
    }

    private Range getRange(@NotNull FilterData inFilter, int defaultRangeValue) throws FilterException {
        if (inFilter.isUseDefaultPeriod()) {
            return Range.createForLastMonthsIncludingCurrent(defaultRangeValue);
        }
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern(FilterData.DATE_FORMAT);
        final String fromDate = inFilter.getFromDate();
        final String toDate = inFilter.getToDate();

        if (fromDate == null || toDate == null) {
            throw new FilterException(String.format("Can not parse null range dates. From: {}, To: {}", fromDate, toDate));
        }
        try {
            final LocalDate from = dateStringFormat.parseLocalDate(fromDate);
            final LocalDate to = dateStringFormat.parseLocalDate(toDate);
            validateFilterRange(from, to);
            return new Range(from.withDayOfMonth(1), to.dayOfMonth().withMaximumValue());
        } catch (IllegalArgumentException e) {
            throw new FilterException(String.format("Could not parse range dates. From: {}, To: {}", fromDate, toDate), e);
        }
    }

    private void validateFilterRange(LocalDate from, LocalDate to) throws FilterException {
        final LocalDate lowestAcceptedStartDate = new LocalDate(2013, 10, 1);
        if (from.isBefore(lowestAcceptedStartDate)) {
            throw new FilterException("Start date before 2013-10-01 is not allowed");
        }
        if (to.isBefore(from)) {
            throw new FilterException("Start date must be before end date");
        }
        if (to.isAfter(new LocalDate().dayOfMonth().withMaximumValue())) {
            throw new FilterException("End date may not be after the last day of current month");
        }
    }

    private Filter getFilterForAllAvailableEnhetsLandsting(HttpServletRequest request) {
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        final String vardgivarId = info.getDefaultVerksamhet().getVardgivarId();
        final List<String> enhets = landstingEnhetHandler.getAllEnhetsForVardgivare(vardgivarId);
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(enhets, new Function<String, Integer>() {
            @Override
            public Integer apply(String enhetid) {
                return Warehouse.getEnhet(enhetid);
            }
        }));
        return getFilterForEnhets(availableEnhets, enhets);
    }

    private Filter getFilterForEnhets(final Set<Integer> enhetsIntIds, List<String> enhets) {
        return new Filter(new SjukfallFilter(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return enhetsIntIds.contains(fact.getEnhet());
            }
        }, SjukfallFilter.getHashValueForEnhets(enhetsIntIds.toArray())), enhets, null);
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
        return getFilterForEnhets(availableEnhets, null);
    }

    private ArrayList<String> getEnhetsFilteredLandsting(HttpServletRequest request, FilterData inFilter) {
        Set<String> enhetsMatchingVerksamhetstyp = getEnhetsForVerksamhetstyperLandsting(inFilter, request);
        final HashSet<String> enhets = new HashSet<>(inFilter.getEnheter());
        return new ArrayList<>(Sets.intersection(enhetsMatchingVerksamhetstyp, enhets));
    }

    private ArrayList<String> getEnhetsFiltered(HttpServletRequest request, FilterData inFilter) {
        Set<String> enhetsMatchingVerksamhetstyp = getEnhetsForVerksamhetstyper(inFilter.getVerksamhetstyper(), request);
        final HashSet<String> enhets = new HashSet<>(inFilter.getEnheter());
        return new ArrayList<>(Sets.intersection(enhetsMatchingVerksamhetstyp, enhets));
    }

    private Set<String> getEnhetsForVerksamhetstyperLandsting(FilterData filterData, HttpServletRequest request) {
        final List<Enhet> enhets = enhetManager.getEnhets(filterData.getEnheter());
        Set<String> enhetsIds = new HashSet<>();
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        for (Enhet verksamhet : enhets) {
            if (isOfVerksamhetsTypLandsting(verksamhet, filterData.getVerksamhetstyper())) {
                enhetsIds.add(verksamhet.getEnhetId());
            }
        }
        return enhetsIds;
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

    private boolean isOfVerksamhetsTypLandsting(Enhet verksamhet, List<String> verksamhetstyper) {
        final Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet = loginServiceUtil.getVerksamhetsTyper(verksamhet.getVerksamhetsTyper());
        return isOfVerksamhetsTyp(verksamhetstyper, verksamhetstyperForCurrentVerksamhet);
    }

    private boolean isOfVerksamhetsTyp(List<String> verksamhetstyper, Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet) {
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

    private boolean isOfVerksamhetsTyp(Verksamhet verksamhet, List<String> verksamhetstyper) {
        final Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet = verksamhet.getVerksamhetsTyper();
        return isOfVerksamhetsTyp(verksamhetstyper, verksamhetstyperForCurrentVerksamhet);
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

    public boolean hasAccessToLandstingAdmin(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return loginServiceUtil.getLoginInfo(request).isLandstingAdmin();
    }

    public boolean hasAccessToLandsting(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return loginServiceUtil.getLoginInfo(request).isLandstingsvardgivare();
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
