/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.landsting.LandstingEnhetFileData;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.NoLandstingSetForVgException;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdate;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateOperation;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.LandstingsData;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.service.landsting.LandstingEnhetFileParseException;
import se.inera.statistics.web.service.landsting.LandstingFileGenerationException;
import se.inera.statistics.web.service.landsting.LandstingFileReader;
import se.inera.statistics.web.service.landsting.LandstingFileWriter;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

/**
 * Statistics services that requires authorization to use. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv and xlsx variants
 * only contains one data set.
 * <p/>
 * They all return 403 if called outside of a session or if authorization fails.
 */
@Service("protectedLandstingService")
@Path("/landsting")
public class ProtectedLandstingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProtectedLandstingService.class);
    public static final int YEAR = 12;

    @Autowired
    private WarehouseService warehouse;

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    @Autowired
    private LandstingEnhetHandler landstingEnhetHandler;

    @Autowired
    private FilterHandler filterHandler;

    @Autowired
    private EnhetManager enhetManager;

    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Autowired
    private ResponseHandler responseHandler;

    private LandstingFileReader landstingFileReader = new LandstingFileReader();

    private LandstingFileWriter landstingFileWriter = new LandstingFileWriter();

    @POST
    @Path("fileupload")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
    @Consumes({ "multipart/form-data" })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response fileupload(@Context HttpServletRequest request, MultipartBody body) {
        LoginInfo info = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final LoginInfoVg loginInfoVg = info.getLoginInfoForVg(vgId).orElse(LoginInfoVg.empty());
        final String fallbackUpload = body.getAttachmentObject("fallbackUpload", String.class);
        final boolean isUsingClassicFormFileUpload = fallbackUpload != null;
        final UploadResultFormat resultFormat = isUsingClassicFormFileUpload ? UploadResultFormat.HTML : UploadResultFormat.JSON;
        if (!loginInfoVg.isProcessledare()) {
            final String msg = "A user without processledar-status tried to update landstingsdata";
            LOG.warn(msg + " : " + info.getHsaId());
            return createFileUploadResponse(Response.Status.FORBIDDEN, msg, resultFormat);
        }
        final DataSource dataSource = body.getAttachment("file").getDataHandler().getDataSource();
        if (dataSource.getName() == null) {
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, "Fil saknas", resultFormat);
        }
        try {
            final List<LandstingEnhetFileDataRow> landstingFileRows = landstingFileReader.readExcelData(dataSource);
            final HsaIdVardgivare vardgivarId = loginInfoVg.getHsaId();
            final LandstingEnhetFileData fileData = new LandstingEnhetFileData(vardgivarId, landstingFileRows, info.getName(),
                    info.getHsaId(), dataSource.getName());
            landstingEnhetHandler.update(fileData);

            monitoringLogService.logFileUpload(info.getHsaId(), vardgivarId, dataSource.getName(), landstingFileRows.size());

            return createFileUploadResponse(Response.Status.OK, "Data updated ok", resultFormat);
        } catch (LandstingEnhetFileParseException e) {
            LOG.warn("Failed to parse landstings file", e);
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), resultFormat);
        } catch (NoLandstingSetForVgException e) {
            LOG.warn("Failed to update landsting settings", e);
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Din vårdgivare har inte tillgång till landstingsstatistik", resultFormat);
        }
    }

    @DELETE
    @Path("landstingEnhets")
    @Consumes({ MediaType.WILDCARD })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response clearLandstingEnhets(@Context HttpServletRequest request) {
        LoginInfo info = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        try {
            landstingEnhetHandler.clear(vgId, info.getName(), info.getHsaId());
            return Response.ok().build();
        } catch (NoLandstingSetForVgException e) {
            LOG.warn("Failed to clear landsting settings", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Din vårdgivare har inte tillgång till landstingsstatistik").build();
        }
    }

    @GET
    @Path("prepopulatedLandstingFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getPrepopulatedLandstingFile(@Context HttpServletRequest request) {
        final HsaIdVardgivare vardgivarId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<Enhet> enhets = enhetManager.getAllEnhetsForVardgivareId(vardgivarId);
        try {
            final ByteArrayOutputStream generatedFile = landstingFileWriter.generateExcelFile(enhets);
            return Response.ok(generatedFile.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + vardgivarId + "_landsting.xlsx\"").build();
        } catch (LandstingFileGenerationException e) {
            LOG.debug("File generation failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not generate excel file").build();
        }
    }

    @GET
    @Path("emptyLandstingFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getEmptyLandstingFile(@Context HttpServletRequest request) {
        try {
            final ByteArrayOutputStream generatedFile = landstingFileWriter.generateExcelFile(Collections.<Enhet> emptyList());
            return Response.ok(generatedFile.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"mall_landsting.xlsx\"").build();
        } catch (LandstingFileGenerationException e) {
            LOG.debug("Empty file generation failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could generate empty landsting excel file").build();
        }
    }

    @GET
    @Path("lastUpdateInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getLastLandstingUpdateInfo(@Context HttpServletRequest request) {
        final HsaIdVardgivare vardgivarId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final HashMap<String, Object> result = new HashMap<>();

        result.put("infoMessage", getLastLandstingUpdateInfoMessage(vardgivarId));

        final List<LandstingEnhet> landstingEnhets = landstingEnhetHandler.getAllLandstingEnhetsForVardgivare(vardgivarId);
        final List<String> parsedRowsStrings = landstingEnhets.stream().map(landstingEnhetFileDataRow -> {
            final Integer listadePatienter = landstingEnhetFileDataRow.getListadePatienter();
            final String listadePatienterString = listadePatienter != null ? String.valueOf(listadePatienter) : "inte angivet";
            return "HSA-id: " + landstingEnhetFileDataRow.getEnhetensHsaId() + " -> Listade patienter: " + listadePatienterString;
        }).collect(Collectors.toList());
        result.put("parsedRows", parsedRowsStrings);

        return Response.ok(result).build();
    }

    private String getLastLandstingUpdateInfoMessage(HsaIdVardgivare vardgivarId) {
        final Optional<LandstingEnhetUpdate> lastUpdateInfo = landstingEnhetHandler.getLastUpdateInfo(vardgivarId);
        if (lastUpdateInfo.isPresent()) {
            final LandstingEnhetUpdate update = lastUpdateInfo.get();
            final LandstingEnhetUpdateOperation operation = update.getOperation();
            String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date(update.getTimestamp().getTime()));
            return operation.getMessage()
                    + (LandstingEnhetUpdateOperation.UPDATE.equals(operation) ? " (" + update.getFilename() + ")" : "") + " - " + dateTime
                    + " av " + update.getUpdatedByName() + " (" + update.getUpdatedByHsaid() + ")";
        }
        return "Ingen";
    }

    private String getLastLandstingUpdateDate(HsaIdVardgivare vardgivarId) {
        final Optional<LandstingEnhetUpdate> lastUpdateInfo = landstingEnhetHandler.getLastUpdateInfo(vardgivarId);
        if (lastUpdateInfo.isPresent()) {
            final LandstingEnhetUpdate update = lastUpdateInfo.get();
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
                    + "</div><br/><input type='button' onclick='history.back();' value='Åter till statistiktjänsten'></body></html>";
            return Response.status(Response.Status.OK).type(MediaType.TEXT_HTML + "; charset=utf-8").entity(html).build();
        case JSON:
            final HashMap<String, Object> map = new HashMap<>();
            map.put("message", message);
            return Response.status(status).entity(map).build();
        default:
            throw new RuntimeException("Unhandled upload result format: " + format);
        }
    }

    @GET
    @Path("getNumberOfCasesPerMonthLandsting")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getNumberOfCasesPerMonthLandsting(@Context HttpServletRequest request, @QueryParam("landstingfilter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilterForLandsting(request, filterHash, 18);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonthLandsting(filterSettings);
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final SimpleDetailsData data = new PeriodConverter().convert(casesPerMonth, filterSettings);
        final LandstingsData result = LandstingsData.create(data, getLastLandstingUpdateDate(vgIdForLoggedInUser));
        return getResponse(result, format, request, Report.L_SJUKFALLTOTALT);
    }

    @GET
    @Path("getNumberOfCasesPerEnhetLandsting")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhetLandsting(@Context HttpServletRequest request, @QueryParam("landstingfilter") String filterHash,
            @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilterForLandsting(request, filterHash, 12);
        final List<HsaIdEnhet> connectedEnhetIds = getEnhetIdsToMark(request);
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerEnhetLandsting(filterSettings);
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final SimpleDetailsData data = new GroupedSjukfallWithLandstingSortingConverter("Vårdenhet", connectedEnhetIds)
                .convert(casesPerEnhet, filterSettings);
        final LandstingsData result = LandstingsData.create(data, getLastLandstingUpdateDate(vgIdForLoggedInUser));
        return getResponse(result, format, request, Report.L_VARDENHET);
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
    @Path("getNumberOfCasesPerPatientsPerEnhetLandsting")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getNumberOfCasesPerPatientsPerEnhetLandsting(@Context HttpServletRequest request,
            @QueryParam("landstingfilter") String filterHash, @QueryParam("format") String format) {
        final FilterSettings filterSettings = filterHandler.getFilterForLandsting(request, filterHash, 12);
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerPatientsPerEnhetLandsting(filterSettings);
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> connectedEnhetIds = getEnhetIdsToMark(request);
        final List<LandstingEnhet> landstingEnhets = landstingEnhetHandler.getAllLandstingEnhetsForVardgivare(vgIdForLoggedInUser);
        final SimpleDetailsData data = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets, connectedEnhetIds)
                .convert(casesPerEnhet, filterSettings, null);
        final LandstingsData result = LandstingsData.create(data, getLastLandstingUpdateDate(vgIdForLoggedInUser));
        return getResponse(result, format, request, Report.L_VARDENHETLISTNINGAR);
    }

    @GET
    @Path("landstingFilterInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getLandstingFilterInfo(@Context HttpServletRequest request) {
        List<Verksamhet> businesses = filterHandler.getAllVerksamhetsForLoggedInLandstingsUser(request);
        final Map<String, Object> result = new HashMap<>();
        result.put("businesses", businesses);
        return Response.ok(result).build();
    }

    private Response getResponse(TableDataReport result, String format, HttpServletRequest request, Report report) {
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> allEnhets = landstingEnhetHandler.getAllEnhetsForVardgivare(vgIdForLoggedInUser);
        return responseHandler.getResponse(result, format, allEnhets, report);
    }

    public boolean hasAccessToLandstingAdmin(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        return loginServiceUtil.getLoginInfo().getLoginInfoForVg(vg).map(LoginInfoVg::isLandstingAdmin).orElse(false);
    }

    public boolean hasAccessToLandsting(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        final HsaIdVardgivare vg = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        return loginServiceUtil.getLoginInfo().getLoginInfoForVg(vg).map(LoginInfoVg::isLandstingsvardgivare).orElse(false);
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
        JSON;
    }

}
