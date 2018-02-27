/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.DiagnosisSubGroupStatisticsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;
import se.inera.statistics.web.service.responseconverter.AndelKompletteringarConverter;
import se.inera.statistics.web.service.responseconverter.AndelKompletteringarTvarsnittConverter;
import se.inera.statistics.web.service.responseconverter.CompareDiagnosisTimeSeriesConverter;
import se.inera.statistics.web.service.responseconverter.DegreeOfSickLeaveConverter;
import se.inera.statistics.web.service.responseconverter.DiagnosisGroupsConverter;
import se.inera.statistics.web.service.responseconverter.DiagnosisGroupsTvarsnittConverter;
import se.inera.statistics.web.service.responseconverter.DiagnosisSubGroupsConverter;
import se.inera.statistics.web.service.responseconverter.DiagnosisSubGroupsTvarsnittConverter;
import se.inera.statistics.web.service.responseconverter.GroupedSjukfallConverter;
import se.inera.statistics.web.service.responseconverter.MessageAmneConverter;
import se.inera.statistics.web.service.responseconverter.MessageAmnePerEnhetConverter;
import se.inera.statistics.web.service.responseconverter.MessageAmnePerEnhetTvarsnittConverter;
import se.inera.statistics.web.service.responseconverter.MessageAmneTvarsnittConverter;
import se.inera.statistics.web.service.responseconverter.MessagePeriodConverter;
import se.inera.statistics.web.service.responseconverter.PeriodConverter;
import se.inera.statistics.web.service.responseconverter.PeriodIntygConverter;
import se.inera.statistics.web.service.responseconverter.SimpleDualSexConverter;
import se.inera.statistics.web.service.responseconverter.SimpleMultiDualSexConverter;
import se.inera.statistics.web.service.responseconverter.VerksamhetOverviewConverter;

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
import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Statistics services that requires authorization to use. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv and xlsx variants
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

    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Autowired
    private ResponseHandler responseHandler;

    @Autowired
    private Clock clock;

    private Response getResponse(TableDataReport result, String format, HttpServletRequest request, Report report) {
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();
        final List<Verksamhet> businesses = loginInfo.getBusinessesForVg(loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        final List<HsaIdEnhet> hsaIdEnhets = businesses.stream().map(Verksamhet::getId).collect(Collectors.toList());
        return responseHandler.getResponse(result, format, hsaIdEnhets, report);
    }

    /**
     * Gets sjukfall per manad for verksamhetId.
     */
    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerMonth(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
                                             @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        SimpleKonResponse casesPerMonth = warehouse.getCasesPerMonth(filterSettings.getFilter().getPredicate(),
                filterSettings.getRange(), loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new PeriodConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_SJUKFALLTOTALT);
    }

    @GET
    @Path("getNumberOfCasesPerMonthTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerMonthTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
                                                      @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse casesPerMonth = warehouse.getCasesPerMonthTvarsnitt(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = SimpleDualSexConverter.newGenericTvarsnitt().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_SJUKFALLTOTALT);
    }

    /**
     * Gets intyg per manad for verksamhetId.
     */
    @GET
    @Path("getTotalNumberOfIntygPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getTotalNumberOfIntygPerMonth(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        SimpleKonResponse intygPerMonth = warehouse.getTotalIntygPerMonth(vg, filterSettings);
        SimpleDetailsData result = new PeriodIntygConverter().convert(intygPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_INTYGPERMANAD);
    }

    /**
     * Gets intyg per manad tvärsnitt for verksamhetId.
     */
    @GET
    @Path("getTotalNumberOfIntygTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getTotalNumberOfIntygTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        SimpleKonResponse intygPerMonth = warehouse.getTotalIntygTvarsnitt(vg, filterSettings);
        SimpleDetailsData result = SimpleDualSexConverter.newGenericIntygTvarsnitt().convert(intygPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_INTYGPERMANAD);
    }

    /**
     * Gets intyg per manad for verksamhetId.
     */
    @GET
    @Path("getIntygPerTypePerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfIntygPerTypePerMonth(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        KonDataResponse intygPerMonth = warehouse.getIntygPerTypePerMonth(vg, filterSettings);
        final DualSexStatisticsData result = new SimpleMultiDualSexConverter("Antal intyg totalt").convert(intygPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_INTYGPERTYP);
    }

    /**
     * Gets intyg per manad tvärsnitt for verksamhetId.
     */
    @GET
    @Path("getIntygPerTypeTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfIntygPerTypeTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        SimpleKonResponse intygPerMonth = warehouse.getIntygPerTypeTvarsnitt(vg, filterSettings);
        SimpleDetailsData result = SimpleDualSexConverter.newGenericIntygTvarsnitt().convert(intygPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_INTYGPERTYP);
    }

    /**
     * Gets meddelanden per manad for verksamhetId.
     */
    @GET
    @Path("getNumberOfMeddelandenPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfMeddelandenPerMonth(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        SimpleKonResponse casesPerMonth = warehouse.getMessagesPerMonth(filterSettings.getFilter(),
                filterSettings.getRange(), loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = MessagePeriodConverter.newTidsserie().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENTOTALT);
    }

    @GET
    @Path("getNumberOfMeddelandenPerMonthTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfMeddelandenPerMonthTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse casesPerMonth = warehouse.getMessagesPerMonthTvarsnitt(filter, range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = MessagePeriodConverter.newTvarsnitt().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENTOTALT);
    }

    @GET
    @Path("getMeddelandenPerAmne")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getMeddelandenPerAmne(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        KonDataResponse casesPerMonth = warehouse.getMessagesPerAmne(filterSettings.getFilter(),
                filterSettings.getRange(), loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new MessageAmneConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENPERAMNE);
    }

    @GET
    @Path("getMeddelandenPerAmneTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getMeddelandenPerAmneTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse casesPerMonth = warehouse.getMessagesPerAmneTvarsnitt(filter, range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = MessageAmneTvarsnittConverter.newTvarsnitt().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENPERAMNE);
    }

    @GET
    @Path("getMeddelandenPerAmnePerEnhet")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getMeddelandenPerAmnePerEnhet(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        KonDataResponse casesPerMonth = warehouse.getMessagesPerAmnePerEnhet(filterSettings.getFilter(),
                filterSettings.getRange(), loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new MessageAmnePerEnhetConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENPERAMNE);
    }

    @GET
    @Path("getMeddelandenPerAmnePerEnhetTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getMeddelandenPerAmnePerEnhetTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse casesPerMonth = warehouse.getMessagesPerAmnePerEnhetTvarsnitt(filter, range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new MessageAmnePerEnhetTvarsnittConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENPERAMNE);
    }

    @GET
    @Path("getAndelKompletteringar")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAndelKompletteringar(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
                                          @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        KonDataResponse casesPerMonth = warehouse.getAndelKompletteringar(filterSettings.getFilter(),
                filterSettings.getRange(), loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new AndelKompletteringarConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENPERAMNE);
    }

    @GET
    @Path("getAndelKompletteringarTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAndelKompletteringarTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
                                                   @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse casesPerMonth = warehouse.getAndelKompletteringarTvarsnitt(filter, range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = AndelKompletteringarTvarsnittConverter.newTvarsnitt().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.V_MEDDELANDENPERAMNE);
    }

    /**
     * Gets sjukfall per enhet for verksamhetId.
     */
    @GET
    @Path("getNumberOfCasesPerEnhet")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhet(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final List<HsaIdEnhet> enhetsFilterIds = filterHandler.getEnhetsFilterIds(filter.getEnheter(), request);
        Map<HsaIdEnhet, String> idToNameMap = filterHandler.getEnhetNameMap(request, enhetsFilterIds);
        SimpleKonResponse casesPerEnhet = warehouse.getCasesPerEnhet(filter.getPredicate(), idToNameMap, range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new GroupedSjukfallConverter("").convert(casesPerEnhet, filterSettings);
        return getResponse(result, format, request, Report.V_VARDENHET);
    }

    /**
     * Gets sjukfall per enhet for verksamhetId i tidsserie.
     */
    @GET
    @Path("getNumberOfCasesPerEnhetTimeSeries")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhetTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final List<HsaIdEnhet> enhetsFilterIds = filterHandler.getEnhetsFilterIds(filter.getEnheter(), request);
        Map<HsaIdEnhet, String> idToNameMap = filterHandler.getEnhetNameMap(request, enhetsFilterIds);
        KonDataResponse casesPerEnhet = warehouse.getCasesPerEnhetTimeSeries(filter.getPredicate(), idToNameMap, range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new SimpleMultiDualSexConverter().convert(casesPerEnhet, filterSettings);
        return getResponse(result, format, request, Report.V_VARDENHET);
    }

    /**
     * Gets sjukfall per doctor for verksamhetId.
     */
    @GET
    @Path("getNumberOfCasesPerLakare")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakare(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse casesPerLakare = warehouse.getCasesPerLakare(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        final SimpleDetailsData result = new GroupedSjukfallConverter("").convert(casesPerLakare, filterSettings);
        return getResponse(result, format, request, Report.V_SJUKFALLPERLAKARE);
    }

    @GET
    @Path("getSjukfallPerLakareSomTidsserie")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakareSomTidsserie(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse casesPerLakare = warehouse.getCasesPerLakareSomTidsserie(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        final DualSexStatisticsData result = new SimpleMultiDualSexConverter().convert(casesPerLakare, filterSettings);
        return getResponse(result, format, request, Report.V_SJUKFALLPERLAKARE);
    }

    /**
     * Get sjukfall per diagnoskapitel and per diagnosgrupp. The chart data is grouped by diagnosgrupp,
     * the table data by diagnoskapitel. Diagnosgrupp is a diagnoskapitel or a list of diagnoskapitel.
     */
    @GET
    @Path("getDiagnoskapitelstatistik")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisGroupStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        DiagnosgruppResponse diagnosisGroups = warehouse.getDiagnosgrupperPerMonth(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData result = new DiagnosisGroupsConverter().convert(diagnosisGroups, filterSettings);
        return getResponse(result, format, request, Report.V_DIAGNOSGRUPP);
    }

    @GET
    @Path("getDiagnosGruppTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisGroupTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse diagnosisGroups = warehouse.getDiagnosgrupperTvarsnitt(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData result = new DiagnosisGroupsTvarsnittConverter().convert(diagnosisGroups, filterSettings);
        return getResponse(result, format, request, Report.V_DIAGNOSGRUPP);
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisSubGroupStatistics(@Context HttpServletRequest request, @PathParam("groupId") String groupId,
            @QueryParam("filter") String filterHash, @QueryParam("format") String format) {
        try {
            final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
            final Filter filter = filterSettings.getFilter();
            final Range range = filterSettings.getRange();
            final DiagnosgruppResponse diagnosavsnitt = warehouse.getUnderdiagnosgrupper(filter.getPredicate(), range, groupId,
                    loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
            final Icd10.Id icd = icd10.findFromIcd10Code(groupId);
            final Message message = getDiagnosisSubGroupStatisticsMessage(filter,
                    Collections.singletonList(String.valueOf(icd.toInt())));
            final DualSexStatisticsData data = new DiagnosisSubGroupsConverter().convert(diagnosavsnitt, filterSettings, message);
            final DiagnosisSubGroupStatisticsData result = new DiagnosisSubGroupStatisticsData(data, icd);
            return getResponse(result, format, request, Report.V_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL);
        } catch (RangeNotFoundException e) {
            LOG.debug("Range not found", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private Message getDiagnosisSubGroupStatisticsMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {
            String msg = "Du har gjort ett val av diagnoskapitel eller diagnosavsnitt som inte matchar det val du gjort i diagnosfilter "
                    + "(se Visa filter högst upp på sidan).";
            return Message.create(ErrorType.FILTER, ErrorSeverity.WARN, msg);
        }
        return null;
    }

    @GET
    @Path("getDiagnosavsnittTvarsnitt/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDiagnosisSubGroupTvarsnitt(@Context HttpServletRequest request, @PathParam("groupId") String groupId,
            @QueryParam("filter") String filterHash, @QueryParam("format") String format) {
        try {
            final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
            final Filter filter = filterSettings.getFilter();
            final Range range = filterSettings.getRange();
            final SimpleKonResponse diagnosavsnitt = warehouse.getUnderdiagnosgrupperTvarsnitt(filter.getPredicate(),
                    range, groupId, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
            final Message message = getDiagnosisSubGroupStatisticsMessage(filter,
                    Collections.singletonList(String.valueOf(icd10.findFromIcd10Code(groupId).toInt())));
            final SimpleDetailsData data = new DiagnosisSubGroupsTvarsnittConverter().convert(diagnosavsnitt, filterSettings, message);
            return getResponse(data, format, request, Report.V_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL);
        } catch (RangeNotFoundException e) {
            LOG.debug("Range not found", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getJamforDiagnoserStatistik/{diagnosHash}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatistics(@Context HttpServletRequest request, @PathParam("diagnosHash") String diagnosisHash,
            @QueryParam("filter") String filterHash, @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String> emptyList()
                : filterHashHandler.getFilterFromHash(diagnosisHash).getDiagnoser();
        final Message message = emptyDiagnosisHash ? createMessage("Inga diagnoser valda") : getCompareDiagnosisMessage(filter, diagnosis);
        SimpleKonResponse resultRows = warehouse.getJamforDiagnoser(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request), diagnosis);
        SimpleDetailsData data = SimpleDualSexConverter.newGenericTvarsnitt().convert(resultRows, filterSettings, message);
        return getResponse(data, format, request, Report.V_DIAGNOSGRUPPJAMFORVALFRIADIAGNOSER);
    }

    private Message getCompareDiagnosisMessage(Filter filter, List<String> diagnosis) {
        if (resultMessageHandler.isDxFilterDisableAllSelectedDxs(diagnosis, filter.getDiagnoser())) {

            String msg = "Du har gjort ett val av diagnos som inte matchar det val du gjort i diagnosfilter "
                    + "(se Visa filter högst upp på sidan).";
            return Message.create(ErrorType.FILTER, ErrorSeverity.WARN, msg);
        }
        return null;
    }

    @GET
    @Path("getJamforDiagnoserStatistikTidsserie/{diagnosHash}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCompareDiagnosisStatisticsTimeSeries(@Context HttpServletRequest request,
            @PathParam("diagnosHash") String diagnosisHash, @QueryParam("filter") String filterHash, @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        final boolean emptyDiagnosisHash = diagnosisHash == null || diagnosisHash.isEmpty();
        final List<String> diagnosis = emptyDiagnosisHash ? Collections.<String> emptyList()
                : filterHashHandler.getFilterFromHash(diagnosisHash).getDiagnoser();
        final Message message = emptyDiagnosisHash ? createMessage("Inga diagnoser valda") : getCompareDiagnosisMessage(filter, diagnosis);
        KonDataResponse resultRows = warehouse.getJamforDiagnoserTidsserie(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request), diagnosis);
        DualSexStatisticsData data = new CompareDiagnosisTimeSeriesConverter().convert(resultRows, filterSettings, message);
        return getResponse(data, format, request, Report.V_DIAGNOSGRUPPJAMFORVALFRIADIAGNOSER);
    }

    /**
     * Get overview. Includes total n:o of sjukfall, sex distribution, top lists for diagnosgrupp, aldersgrupp,
     * sjukskrivningslangd,
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
        final Range range = Range.quarter(clock);
        final Message message = getOverviewMsg(filterHash, range);
        VerksamhetOverviewResponse response = warehouse.getOverview(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        final VerksamhetOverviewData overviewData = new VerksamhetOverviewConverter().convert(response, range, filter, message);

        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();
        final List<Verksamhet> businesses = loginInfo.getBusinessesForVg(loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        final List<HsaIdEnhet> hsaIdEnhets = businesses.stream().map(Verksamhet::getId).collect(Collectors.toList());
        return responseHandler.getResponseForDataReport(overviewData, hsaIdEnhets);
    }

    private Message getOverviewMsg(String filterHash, Range actualRangeUsed) {
        final boolean noMsg = filterHash == null || filterHash.isEmpty()
                || filterHashHandler.getFilterFromHash(filterHash).isUseDefaultPeriod();
        if (noMsg) {
            return null;
        }
        final String msg = "Översikten visar alltid de senaste tre avslutade kalendermånaderna ("
                + actualRangeUsed.toStringShortMonths()
                + ") oavsett valt tidsintervall.";
        return Message.create(ErrorType.FILTER, ErrorSeverity.INFO, msg);
    }

    /**
     * Get sjukfall grouped by age and sex.
     */
    @GET
    @Path("getAgeGroupsStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse ageGroups = warehouse.getAldersgrupper(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = SimpleDualSexConverter.newGenericTvarsnitt().convert(ageGroups, filterSettings);
        return getResponse(data, format, request, Report.V_ALDERSGRUPP);
    }

    @GET
    @Path("getAgeGroupsStatisticsAsTimeSeries")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getAgeGroupsStatisticsAsTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getAldersgrupperSomTidsserie(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(ageGroups, filterSettings);
        return getResponse(data, format, request, Report.V_ALDERSGRUPP);
    }

    /**
     * Get sjukfall grouped by age and sex of the doctor.
     */
    @GET
    @Path("getCasesPerDoctorAgeAndGenderStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCasesPerDoctorAgeAndGenderStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse ageGroups = warehouse.getCasesPerDoctorAgeAndGender(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = SimpleDualSexConverter.newGenericTvarsnitt().convert(ageGroups, filterSettings);
        return getResponse(data, format, request, Report.V_LAKARALDEROCHKON);
    }

    @GET
    @Path("getCasesPerDoctorAgeAndGenderTimeSeriesStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getCasesPerDoctorAgeAndGenderTimeSeriesStatistics(@Context HttpServletRequest request,
            @QueryParam("filter") String filterHash, @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getCasesPerDoctorAgeAndGenderTimeSeries(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(ageGroups, filterSettings);
        return getResponse(data, format, request, Report.V_LAKARALDEROCHKON);
    }

    /**
     * Get sjukfall grouped by doctor grade.
     */
    @GET
    @Path("getNumberOfCasesPerLakarbefattning")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakarbefattning(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse ageGroups = warehouse.getNumberOfCasesPerLakarbefattning(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = SimpleDualSexConverter.newGenericTvarsnitt().convert(ageGroups, filterSettings);
        return getResponse(data, format, request, Report.V_LAKARBEFATTNING);
    }

    @GET
    @Path("getNumberOfCasesPerLakarbefattningSomTidsserie")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getNumberOfCasesPerLakarbefattningSomTidsserie(@Context HttpServletRequest request,
            @QueryParam("filter") String filterHash, @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse ageGroups = warehouse.getNumberOfCasesPerLakarbefattningSomTidsserie(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(ageGroups, filterSettings);
        return getResponse(data, format, request, Report.V_LAKARBEFATTNING);
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDegreeOfSickLeaveStatistics(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradPerMonth(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, filterSettings);
        return getResponse(data, format, request, Report.V_SJUKSKRIVNINGSGRAD);
    }

    @GET
    @Path("getDegreeOfSickLeaveTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getDegreeOfSickLeaveTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse degreeOfSickLeaveStatistics = warehouse.getSjukskrivningsgradTvarsnitt(filter.getPredicate(),
                range, loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new SimpleDualSexConverter("", "%1$s %% sjukskrivningsgrad").convert(degreeOfSickLeaveStatistics,
                filterSettings);
        return getResponse(data, format, request, Report.V_SJUKSKRIVNINGSGRAD);
    }

    /**
     * Get sjukfallslangd (grouped).
     */
    @GET
    @Path("getSickLeaveLengthData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLength(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse sickLeaveLength = warehouse.getSjukskrivningslangd(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = SimpleDualSexConverter.newGenericTvarsnitt().convert(sickLeaveLength, filterSettings);
        return getResponse(data, format, request, Report.V_SJUKSKRIVNINGSLANGD);
    }

    @GET
    @Path("getSickLeaveLengthTimeSeries")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getSickLeaveLengthTimeSeries(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        KonDataResponse sickLeaveLength = warehouse.getSjukskrivningslangdTidsserie(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        DualSexStatisticsData data = new SimpleMultiDualSexConverter().convert(sickLeaveLength, filterSettings);
        return getResponse(data, format, request, Report.V_SJUKSKRIVNINGSLANGD);
    }

    /**
     * Gets sjukfallslangd, grouped by sex, long / not long sjukfall.
     */
    @GET
    @Path("getLongSickLeavesData")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getLongSickLeavesData(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 18);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse longSickLeaves = warehouse.getLangaSjukskrivningarPerManad(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = new PeriodConverter().convert(longSickLeaves, filterSettings);
        return getResponse(data, format, request, Report.V_SJUKSKRIVNINGSLANGDMERAN90DAGAR);
    }

    @GET
    @Path("getLongSickLeavesTvarsnitt")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedChartDataService.hasAccessTo(#request)")
    @PostAuthorize(value = "@protectedChartDataService.userAccess(#request)")
    public Response getLongSickLeavesTvarsnitt(@Context HttpServletRequest request, @QueryParam("filter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilter(request, filterHash, 12);
        final Filter filter = filterSettings.getFilter();
        final Range range = filterSettings.getRange();
        SimpleKonResponse longSickLeaves = warehouse.getLangaSjukskrivningarTvarsnitt(filter.getPredicate(), range,
                loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        SimpleDetailsData data = SimpleDualSexConverter.newGenericTvarsnitt().convert(longSickLeaves, filterSettings);
        return getResponse(data, format, request, Report.V_SJUKSKRIVNINGSLANGDMERAN90DAGAR);
    }

    public boolean hasAccessTo(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgid = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final boolean userHasAccessToRequestedVg = loginInfo.getVgs().stream().map(LoginInfoVg::getHsaId)
                .anyMatch(userVg -> userVg.equals(vgid));
        return loginInfo.isLoggedIn() && userHasAccessToRequestedVg;
    }

    public boolean userAccess(HttpServletRequest request) {
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare hsaIdVardgivare = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        String sessionId = request.getSession() != null ? request.getSession().getId() : null;
        LOG.info("User " + loginInfo.getHsaId() + " accessed verksamhet " + hsaIdVardgivare + " (" + getUriSafe(request) + ") session "
                + sessionId);
        monitoringLogService.logTrackAccessProtectedChartData(loginInfo.getHsaId(), hsaIdVardgivare, getUriSafe(request));
        return true;
    }

    private String getUriSafe(HttpServletRequest request) {
        if (request == null) {
            return "!NoRequest!";
        }
        return request.getRequestURI();
    }

    private Message createMessage(String msg) {
        return Message.create(ErrorType.UNSET, ErrorSeverity.INFO, msg);
    }

}
