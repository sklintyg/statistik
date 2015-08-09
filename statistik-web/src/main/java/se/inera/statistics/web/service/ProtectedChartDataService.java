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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private Icd10 icd10;

    @Autowired
    private FilterHashHandler filterHashHandler;

    @Autowired
    private ResultMessageHandler resultMessageHandler;

    @Autowired
    private FilterHandler filterHandler;

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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonth(filterSettings.getFilter().getPredicate(), filterSettings.getRange(), loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonthTvarsnitt(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        Map<HsaIdEnhet, String> idToNameMap = filterHandler.getEnhetNameMap(request, filterHandler.getEnhetsFilterIds(filterHash, request));
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerEnhet(filter.getPredicate(), idToNameMap, range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        Map<HsaIdEnhet, String> idToNameMap = filterHandler.getEnhetNameMap(request, filterHandler.getEnhetsFilterIds(filterHash, request));
        KonDataResponse casesPerEnhet = warehouse.getCasesPerEnhetTimeSeries(filter.getPredicate(), idToNameMap, range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new SimpleMultiDualSexConverter().convert(casesPerEnhet, filterSettings);
        return getResponse(result, csv);
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> casesPerLakare = warehouse.getCasesPerLakare(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse casesPerLakare = warehouse.getCasesPerLakareSomTidsserie(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        DiagnosgruppResponse diagnosisGroups = warehouse.getDiagnosgrupperPerMonth(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> diagnosisGroups = warehouse.getDiagnosgrupperTvarsnitt(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
            final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
            final Filter filter = filterSettings.getFilter();
            final Range range = filterSettings.getRange();
            final DiagnosgruppResponse diagnosavsnitt = warehouse.getUnderdiagnosgrupper(filter.getPredicate(), range, groupId, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
            final String message = getDiagnosisSubGroupStatisticsMessage(filter, Collections.singletonList(String.valueOf(icd10.findFromIcd10Code(groupId).toInt())));
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
            final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
            final Filter filter = filterSettings.getFilter();
            final Range range = filterSettings.getRange();
            final SimpleKonResponse<SimpleKonDataRow> diagnosavsnitt = warehouse.getUnderdiagnosgrupperTvarsnitt(filter.getPredicate(), range, groupId, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
            final String message = getDiagnosisSubGroupStatisticsMessage(filter, Collections.singletonList(String.valueOf(icd10.findFromIcd10Code(groupId).toInt())));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String>emptyList() : filterHashHandler.getFilterFromHash(diagnosisHash).getDiagnoser();
        final String message = emptyDiagnosisHash ? "Inga diagnoser valda" : getCompareDiagnosisMessage(filter, diagnosis);
        SimpleKonResponse<SimpleKonDataRow> resultRows = warehouse.getJamforDiagnoser(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request), diagnosis);
        SimpleDetailsData data = new CompareDiagnosisConverter().convert(resultRows, filterSettings, message);
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String>emptyList() : filterHashHandler.getFilterFromHash(diagnosisHash).getDiagnoser();
        final String message = emptyDiagnosisHash ? "Inga diagnoser valda" : getCompareDiagnosisMessage(filter, diagnosis);
        KonDataResponse resultRows = warehouse.getJamforDiagnoserTidsserie(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request), diagnosis);
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 3);
        final Filter filter = filterSettings.getFilter();
        final Range range = Range.quarter();
        final String message = filterHash == null || filterHash.isEmpty() || filterHashHandler.getFilterFromHash(filterHash).isUseDefaultPeriod() ? null : "Valt tidsintervall i filtret gäller inte för översiktssidan";
        VerksamhetOverviewResponse response = warehouse.getOverview(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getAldersgrupper(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getAldersgrupperSomTidsserie(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getCasesPerDoctorAgeAndGender(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getCasesPerDoctorAgeAndGenderTimeSeries(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> ageGroups = warehouse.getNumberOfCasesPerLakarbefattning(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getNumberOfCasesPerLakarbefattningSomTidsserie(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradPerMonth(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradTvarsnitt(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> sickLeaveLength = warehouse.getSjukskrivningslangd(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse sickLeaveLength = warehouse.getSjukskrivningslangdTidsserie(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> longSickLeaves = warehouse.getLangaSjukskrivningarPerManad(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
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
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> longSickLeaves = warehouse.getLangaSjukskrivningarTvarsnitt(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new LongSickLeaveTvarsnittConverter().convert(longSickLeaves, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getDifferentieratIntygandeStatistics{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDifferentieratIntygandeStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse statisticsData = warehouse.getDifferentieratIntygande(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new DifferentieratIntygandeConverter().convert(statisticsData, filterSettings);
        return getResponse(data, csv);
    }

    @GET
    @Path("getDifferentieratIntygandeTvarsnitt{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDifferentieratIntygandeTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse<SimpleKonDataRow> statisticsData = warehouse.getDifferentieratIntygandeTvarsnitt(filter.getPredicate(), range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new DifferentieratIntygandeTvarsnittConverter().convert(statisticsData, filterSettings);
        return getResponse(data, csv);
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
