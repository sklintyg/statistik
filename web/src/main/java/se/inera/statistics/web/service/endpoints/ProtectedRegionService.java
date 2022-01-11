/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.endpoints;

import static se.inera.statistics.web.service.ReportType.TIDSSERIE;
import static se.inera.statistics.web.service.ReportType.TVARSNITT;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.region.NoRegionSetForVgException;
import se.inera.statistics.service.region.RegionEnhetFileData;
import se.inera.statistics.service.region.RegionEnhetFileDataRow;
import se.inera.statistics.service.region.RegionEnhetHandler;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhet;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdate;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdateOperation;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.MessagesText;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.service.FilterHandler;
import se.inera.statistics.web.service.FilterSettings;
import se.inera.statistics.web.service.LoginServiceUtil;
import se.inera.statistics.web.service.Report;
import se.inera.statistics.web.service.ReportInfo;
import se.inera.statistics.web.service.ReportType;
import se.inera.statistics.web.service.ResponseHandler;
import se.inera.statistics.web.service.WarehouseService;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;
import se.inera.statistics.web.service.region.RegionEnhetFileParseException;
import se.inera.statistics.web.service.region.RegionFileGenerationException;
import se.inera.statistics.web.service.region.RegionFileReader;
import se.inera.statistics.web.service.region.RegionFileWriter;
import se.inera.statistics.web.service.responseconverter.AndelKompletteringarConverter;
import se.inera.statistics.web.service.responseconverter.GroupedSjukfallWithRegionSortingConverter;
import se.inera.statistics.web.service.responseconverter.MessageAmneConverter;
import se.inera.statistics.web.service.responseconverter.MessageAmnePerEnhetTvarsnittConverter;
import se.inera.statistics.web.service.responseconverter.PeriodConverter;
import se.inera.statistics.web.service.responseconverter.SimpleDualSexConverter;
import se.inera.statistics.web.service.responseconverter.SimpleMultiDualSexConverter;
import se.inera.statistics.web.service.responseconverter.SjukfallPerPatientsPerEnhetConverter;

/**
 * Statistics services that requires authorization to use. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv and xlsx variants
 * only contains one data set.
 * <p/>
 * They all return 403 if called outside of a session or if authorization fails.
 */
@Service("protectedRegionService")
@Path("/region")
public class ProtectedRegionService {

    private static final Logger LOG = LoggerFactory.getLogger(ProtectedRegionService.class);
    public static final int YEAR = 12;

    @Autowired
    private WarehouseService warehouse;

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    @Autowired
    private RegionEnhetHandler regionEnhetHandler;

    @Autowired
    private FilterHandler filterHandler;

    @Autowired
    private EnhetManager enhetManager;

    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Autowired
    private ResponseHandler responseHandler;

    private RegionFileReader regionFileReader = new RegionFileReader();

    private RegionFileWriter regionFileWriter = new RegionFileWriter();

    @POST
    @Path("fileupload")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @Consumes({"multipart/form-data"})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegionAdmin(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till uppladdning av filer.")
    public Response fileupload(@Context HttpServletRequest request, MultipartBody body) {
        LoginInfo info = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final LoginInfoVg loginInfoVg = info.getLoginInfoForVg(vgId).orElse(LoginInfoVg.empty());
        final String fallbackUpload = body.getAttachmentObject("fallbackUpload", String.class);
        final boolean isUsingClassicFormFileUpload = fallbackUpload != null;
        final UploadResultFormat resultFormat = isUsingClassicFormFileUpload ? UploadResultFormat.HTML : UploadResultFormat.JSON;
        if (!loginInfoVg.isProcessledare()) {
            final String msg = "A user without processledar-status tried to update regionsdata";
            LOG.warn(msg + " : " + info.getHsaId());
            return createFileUploadResponse(Response.Status.FORBIDDEN, msg, resultFormat);
        }
        final DataSource dataSource = body.getAttachment("file").getDataHandler().getDataSource();
        if (dataSource.getName() == null) {
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, "Fil saknas", resultFormat);
        }
        try {
            final List<RegionEnhetFileDataRow> regionFileRows = regionFileReader.readExcelData(dataSource);
            final HsaIdVardgivare vardgivarId = loginInfoVg.getHsaId();
            final RegionEnhetFileData fileData = new RegionEnhetFileData(vardgivarId, regionFileRows, info.getName(),
                info.getHsaId(), dataSource.getName());
            regionEnhetHandler.update(fileData);

            monitoringLogService.logFileUpload(info.getHsaId(), vardgivarId, dataSource.getName(), regionFileRows.size());

            return createFileUploadResponse(Response.Status.OK, "Data updated ok", resultFormat);
        } catch (RegionEnhetFileParseException e) {
            LOG.warn("Failed to parse regions file", e);
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), resultFormat);
        } catch (NoRegionSetForVgException e) {
            LOG.warn("Failed to update region settings", e);
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "Din vårdgivare har inte tillgång till regionstatistik", resultFormat);
        }
    }

    @DELETE
    @Path("regionEnhets")
    @Consumes({MediaType.WILDCARD})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegionAdmin(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till borttagning av enhet.")
    public Response clearRegionEnhets(@Context HttpServletRequest request) {
        LoginInfo info = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        try {
            regionEnhetHandler.clear(vgId, info.getName(), info.getHsaId());
            return Response.ok().build();
        } catch (NoRegionSetForVgException e) {
            LOG.warn("Failed to clear region settings", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Din vårdgivare har inte tillgång till regionstatistik").build();
        }
    }

    @GET
    @Path("prepopulatedRegionFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegionAdmin(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till excel fil med regionsdata.")
    public Response getPrepopulatedRegionFile(@Context HttpServletRequest request) {
        final HsaIdVardgivare vardgivarId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<Enhet> enhets = enhetManager.getAllVardenhetsForVardgivareId(vardgivarId);
        try {
            final ByteArrayOutputStream generatedFile = regionFileWriter.generateExcelFile(enhets);
            return Response.ok(generatedFile.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + vardgivarId + "_region.xlsx\"").build();
        } catch (RegionFileGenerationException e) {
            LOG.debug("File generation failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not generate excel file").build();
        }
    }

    @GET
    @Path("emptyRegionFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegionAdmin(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till en tom excel fil (mall).")
    public Response getEmptyRegionFile(@Context HttpServletRequest request) {
        try {
            final ByteArrayOutputStream generatedFile = regionFileWriter.generateExcelFile(Collections.<Enhet>emptyList());
            return Response.ok(generatedFile.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"mall_region.xlsx\"").build();
        } catch (RegionFileGenerationException e) {
            LOG.debug("Empty file generation failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could generate empty region excel file").build();
        }
    }

    @GET
    @Path("lastUpdateInfo")
    @Produces({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegionAdmin(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till senaste uppdateringar för region.")
    public Response getLastRegionUpdateInfo(@Context HttpServletRequest request) {
        final HsaIdVardgivare vardgivarId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final HashMap<String, Object> result = new HashMap<>();

        result.put("infoMessage", getLastRegionUpdateInfoMessage(vardgivarId));

        final List<RegionEnhet> regionEnhets = regionEnhetHandler.getAllRegionEnhetsForVardgivare(vardgivarId);
        final List<String> parsedRowsStrings = regionEnhets.stream().map(regionEnhetFileDataRow -> {
            final Integer listadePatienter = regionEnhetFileDataRow.getListadePatienter();
            final String listadePatienterString = listadePatienter != null ? String.valueOf(listadePatienter) : "inte angivet";
            return "HSA-id: " + regionEnhetFileDataRow.getEnhetensHsaId() + " -> Listade patienter: " + listadePatienterString;
        }).collect(Collectors.toList());
        result.put("parsedRows", parsedRowsStrings);

        return Response.ok(result).build();
    }

    private String getLastRegionUpdateInfoMessage(HsaIdVardgivare vardgivarId) {
        final Optional<RegionEnhetUpdate> lastUpdateInfo = regionEnhetHandler.getLastUpdateInfo(vardgivarId);
        if (lastUpdateInfo.isPresent()) {
            final RegionEnhetUpdate update = lastUpdateInfo.get();
            final RegionEnhetUpdateOperation operation = update.getOperation();
            String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(update.getTimestamp().getTime()));
            return operation.getMessage()
                + (RegionEnhetUpdateOperation.UPDATE.equals(operation) ? " (" + update.getFilename() + ")" : "") + " - " + dateTime
                + " av " + update.getUpdatedByName() + " (" + update.getUpdatedByHsaid() + ")";
        }
        return "Ingen";
    }

    private String getLastRegionUpdateDate(HsaIdVardgivare vardgivarId) {
        final Optional<RegionEnhetUpdate> lastUpdateInfo = regionEnhetHandler.getLastUpdateInfo(vardgivarId);
        if (lastUpdateInfo.isPresent()) {
            final RegionEnhetUpdate update = lastUpdateInfo.get();
            return new java.text.SimpleDateFormat("yyyy-MM-dd")
                .format(new java.util.Date(update.getTimestamp().getTime()));
        }
        return "";
    }

    private Response createFileUploadResponse(Response.Status status, String message, UploadResultFormat format) {
        switch (format) {
            case HTML:
                final String statusText = Response.Status.OK.equals(status) ? "Uppladdningen lyckades" : "Uppladdningen misslyckades";
                String html = "<html><body><h1>" + statusText + "</h1><div>" + message
                    + "</div><br/><input type='button' onclick='history.back();' value='Åter till intygsstatistik'></body></html>";
                return Response.status(Response.Status.OK).type(MediaType.TEXT_HTML + "; charset=utf-8").entity(html).build();
            case JSON:
                final HashMap<String, Object> map = new HashMap<>();
                map.put("message", message);
                return Response.status(status).entity(map).build();
        }

        throw new RuntimeException("Unhandled upload result format: " + format);
    }

    @GET
    @Path("getNumberOfCasesPerMonthRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till antal sjukfall per månad och region.")
    public Response getNumberOfCasesPerMonthRegion(@Context HttpServletRequest request, @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 18);
        SimpleKonResponse casesPerMonth = warehouse.getCasesPerMonthRegion(filterSettings);
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final SimpleDetailsData data = new PeriodConverter().convert(casesPerMonth, filterSettings);
        return getResponse(data, format, request, Report.L_SJUKFALLTOTALT, TIDSSERIE, getLastRegionUpdateDate(vgIdForLoggedInUser));
    }

    @GET
    @Path("getNumberOfCasesPerEnhetRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till antal sjukfall per enhet och region.")
    public Response getNumberOfCasesPerEnhetRegion(@Context HttpServletRequest request, @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 12);
        final List<HsaIdEnhet> connectedEnhetIds = getEnhetIdsToMark(request);
        SimpleKonResponse casesPerEnhet = warehouse.getCasesPerEnhetRegion(filterSettings);
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final SimpleDetailsData data = new GroupedSjukfallWithRegionSortingConverter(MessagesText.REPORT_VARDENHET, connectedEnhetIds)
            .convert(casesPerEnhet, filterSettings);
        return getResponse(data, format, request, Report.L_VARDENHET, TVARSNITT, getLastRegionUpdateDate(vgIdForLoggedInUser));
    }

    private List<HsaIdEnhet> getEnhetIdsToMark(@Context HttpServletRequest request) {
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        if (loginInfo.getLoginInfoForVg(vgId).map(LoginInfoVg::isProcessledare).orElse(false)) {
            return Collections.emptyList();
        }
        final List<Verksamhet> businesses = loginInfo.getBusinessesForVg(loginServiceUtil.getSelectedVgIdForLoggedInUser(request));
        return businesses.stream().map(Verksamhet::getId).collect(Collectors.toList());
    }

    @GET
    @Path("getNumberOfCasesPerPatientsPerEnhetRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till antal sjukfall per patienter, enhet och region.")
    public Response getNumberOfCasesPerPatientsPerEnhetRegion(@Context HttpServletRequest request,
        @QueryParam("regionfilter") String filterHash, @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 12);
        SimpleKonResponse casesPerEnhet = warehouse.getCasesPerPatientsPerEnhetRegion(filterSettings);
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> connectedEnhetIds = getEnhetIdsToMark(request);
        final List<RegionEnhet> regionEnhets = regionEnhetHandler.getAllRegionEnhetsForVardgivare(vgIdForLoggedInUser);
        final SimpleDetailsData data = new SjukfallPerPatientsPerEnhetConverter(regionEnhets, connectedEnhetIds)
            .convert(casesPerEnhet, filterSettings, null);
        return getResponse(data, format, request, Report.L_VARDENHETLISTNINGAR, TVARSNITT, getLastRegionUpdateDate(vgIdForLoggedInUser));
    }

    @GET
    @Path("getIntygPerSjukfallTvarsnitt")
    @Produces({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till intyg per sjukfall per region")
    public Response getIntygPerSjukfallTvarsnitt(@Context HttpServletRequest request, @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 12);
        SimpleKonResponse intygPerSjukfall = warehouse.getIntygPerSjukfallTvarsnittRegion(filterSettings);
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final SimpleDetailsData data = new PeriodConverter().convert(intygPerSjukfall, filterSettings);
        return getResponse(data, format, request, Report.L_INTYGPERSJUKFALL, TVARSNITT, getLastRegionUpdateDate(vgIdForLoggedInUser));
    }

    @GET
    @Path("getMeddelandenPerAmneRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till antal meddelanden per ämne och region.")
    public Response getMeddelandenPerAmneRegion(@Context HttpServletRequest request,
        @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 18);
        KonDataResponse casesPerMonth = warehouse.getMessagesPerAmneRegion(filterSettings);
        DualSexStatisticsData result = new MessageAmneConverter().convert(casesPerMonth, filterSettings);
        final String lastRegionUpdateDate = getLastRegionUpdateDate(vgIdForLoggedInUser);
        return getResponse(result, format, request, Report.L_MEDDELANDENPERAMNE, TIDSSERIE, lastRegionUpdateDate);
    }

    @GET
    @Path("getMeddelandenPerAmnePerEnhetRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till ett tvärsnitt av antal meddelanden per ämne och region.")
    public Response getMeddelandenPerAmnePerEnhetTvarsnitt(@Context HttpServletRequest request,
        @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 12);
        KonDataResponse casesPerMonth = warehouse.getMessagesPerAmnePerEnhetRegion(filterSettings);
        SimpleDetailsData result = new MessageAmnePerEnhetTvarsnittConverter().convert(casesPerMonth, filterSettings);
        final String lastRegionUpdateDate = getLastRegionUpdateDate(vgIdForLoggedInUser);
        return getResponse(result, format, request, Report.L_MEDDELANDENPERAMNEPERENHET, TIDSSERIE, lastRegionUpdateDate);
    }

    @GET
    @Path("getIntygPerTypePerMonthRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till antal intyg per type, månad och region.")
    public Response getNumberOfIntygPerTypePerMonthRegion(@Context HttpServletRequest request,
        @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 18);
        KonDataResponse intygPerMonth = warehouse.getIntygPerTypeRegion(filterSettings);
        final String header = "Antal intyg totalt";
        final DualSexStatisticsData result = new SimpleMultiDualSexConverter(header).convert(intygPerMonth, filterSettings);
        return getResponse(result, format, request, Report.L_INTYGPERTYP, TIDSSERIE, getLastRegionUpdateDate(vg));
    }

    @GET
    @Path("getAndelKompletteringarRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till andel kompletteringar per region.")
    public Response getAndelKompletteringarRegion(@Context HttpServletRequest request,
        @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 18);
        KonDataResponse casesPerMonth = warehouse.getAndelKompletteringarRegion(filterSettings);
        DualSexStatisticsData result = new AndelKompletteringarConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.L_ANDELKOMPLETTERINGAR, TIDSSERIE, getLastRegionUpdateDate(vg));
    }

    @GET
    @Path("getKompletteringarPerFragaRegion")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till andel kompletteringar per fråga för region.")
    public Response getKompletteringarPerFragaRegion(@Context HttpServletRequest request,
        @QueryParam("regionfilter") String filterHash,
        @QueryParam("format") String format) {
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final FilterSettings filterSettings = filterHandler.getFilterForRegion(request, filterHash, 18);
        SimpleKonResponse casesPerMonth = warehouse.getKompletteringarPerFragaRegion(filterSettings);
        SimpleDetailsData result = SimpleDualSexConverter.newGenericKompletteringarTvarsnitt().convert(casesPerMonth, filterSettings);
        return getResponse(result, format, request, Report.L_KOMPLETTERINGARPERFRAGA, ReportType.TVARSNITT, getLastRegionUpdateDate(vg));
    }

    @GET
    @Path("regionFilterInfo")
    @Produces({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till filter-info för region.")
    public Response getRegionfilterInfo(@Context HttpServletRequest request) {
        List<Verksamhet> businesses = filterHandler.getAllVerksamhetsForLoggedInRegionsUser(request);
        final Map<String, Object> result = new HashMap<>();
        result.put("businesses", businesses);
        return Response.ok(result).build();
    }

    @PUT
    @Path("acceptFileUploadAgreement")
    @Produces({MediaType.APPLICATION_JSON})
    @PreAuthorize(value = "@protectedRegionService.hasAccessToRegion(#request)")
    @PostAuthorize(value = "@protectedRegionService.userAccess(#request)")
    @PrometheusTimeMethod(
        help = "API-tjänst för skyddad åtkomst till att spara filter-info för region.")
    public Response acceptFileUploadAgreement(@Context HttpServletRequest request) {
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        LOG.info("User accepted region file upload agreement. User: {}, vg: {}", loginInfo.getHsaId(), vgId);
        return Response.ok().build();
    }

    private Response getResponse(TableDataReport result, String format, HttpServletRequest request, Report report, ReportType reportType,
        String fileUploadDate) {
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> allEnhets = regionEnhetHandler.getAllEnhetsForVardgivare(vgIdForLoggedInUser);
        Map<String, Object> extras = new HashMap<>();
        extras.put("fileUploadDate", fileUploadDate);
        return responseHandler.getResponse(result, format, allEnhets, new ReportInfo(report, reportType), extras);
    }

    public boolean hasAccessToRegionAdmin(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        return loginServiceUtil.getLoginInfo().getLoginInfoForVg(vg).map(LoginInfoVg::isRegionAdmin).orElse(false);
    }

    public boolean hasAccessToRegion(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        return loginServiceUtil.getLoginInfo().getLoginInfoForVg(vg).map(LoginInfoVg::isRegionsvardgivare).orElse(false);
    }

    public boolean userAccess(HttpServletRequest request) {
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        LOG.info("User " + loginInfo.getHsaId() + " accessed vg " + vgId + " (" + getUriSafe(request) + ") session "
            + request.getSession().getId());
        return true;
    }

    private String getUriSafe(HttpServletRequest request) {
        if (request == null) {
            return "!NoRequest!";
        }
        return request.getRequestURI();
    }

    private enum UploadResultFormat {
        HTML,
        JSON
    }

}
