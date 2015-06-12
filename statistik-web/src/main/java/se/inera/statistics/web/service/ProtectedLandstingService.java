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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
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
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdate;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateOperation;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistics services that requires authorization to use. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv variants
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

    private LandstingFileReader landstingFileReader = new LandstingFileReader();

    @POST
    @Path("fileupload")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ "multipart/form-data" })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response fileupload(@Context HttpServletRequest request, MultipartBody body) {
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        if (!info.isProcessledare()) {
            final String msg = "A user without processledar-status tried to update landstingsdata";
            LOG.warn(msg + " : " + info.getHsaId());
            return createFileUploadResponse(Response.Status.FORBIDDEN, msg, null);
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
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        } catch (NoLandstingSetForVgException e) {
            LOG.warn("Failed to update landsting settings", e);
            return createFileUploadResponse(Response.Status.INTERNAL_SERVER_ERROR, "Din vårdgivare har inte tillgång till landstingsstatistik", null);
        }
    }

    @GET
    @Path("lastUpdateInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandstingAdmin(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getLastLandstingUpdateInfo(@Context HttpServletRequest request) {
        final String vardgivarId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
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
        return "Ingen";
    }

    private Response createFileUploadResponse(Response.Status status, String message, List<LandstingEnhetFileDataRow> landstingFileRows) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        final List<String> parsedRowsStrings = landstingFileRows == null ? null : Lists.transform(landstingFileRows, new Function<LandstingEnhetFileDataRow, String>() {
            @Override
            public String apply(LandstingEnhetFileDataRow landstingEnhetFileDataRow) {
                final Integer listadePatienter = landstingEnhetFileDataRow.getListadePatienter();
                final String listadePatienterString = listadePatienter != null ? String.valueOf(listadePatienter) : "inte angivet";
                return "HSA-id: " + landstingEnhetFileDataRow.getEnhetensHsaId() + " -> Listade patienter: " + listadePatienterString;
            }
        });
        map.put("parsedRows", parsedRowsStrings);
        return Response.status(status).entity(map).build();
    }

    @GET
    @Path("getNumberOfCasesPerMonthLandsting{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getNumberOfCasesPerMonthLandsting(@Context HttpServletRequest request, @QueryParam("landstingfilter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = filterHandler.getFilterForLandsting(request, filterHash, 18);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = warehouse.getCasesPerMonthLandsting(filterSettings);
        SimpleDetailsData result = new PeriodConverter().convert(casesPerMonth, filterSettings);
        return getResponse(result, csv);
    }

    @GET
    @Path("getNumberOfCasesPerEnhetLandsting{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getNumberOfCasesPerEnhetLandsting(@Context HttpServletRequest request, @QueryParam("landstingfilter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = filterHandler.getFilterForLandsting(request, filterHash, 12);
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerEnhetLandsting(filterSettings);
        SimpleDetailsData result = new GroupedSjukfallWithLandstingSortingConverter("Vårdenhet").convert(casesPerEnhet, filterSettings);
        return getResponse(result, csv);
    }

    @GET
    @Path("getNumberOfCasesPerPatientsPerEnhetLandsting{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getNumberOfCasesPerPatientsPerEnhetLandsting(@Context HttpServletRequest request, @QueryParam("landstingfilter") String filterHash, @PathParam("csv") String csv) {
        final FilterSettings filterSettings = filterHandler.getFilterForLandsting(request, filterHash, 12);
        SimpleKonResponse<SimpleKonDataRow> casesPerEnhet = warehouse.getCasesPerPatientsPerEnhetLandsting(filterSettings);
        final String vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<LandstingEnhet> landstingEnhets = landstingEnhetHandler.getAllLandstingEnhetsForVardgivare(vgIdForLoggedInUser);
        SimpleDetailsData result = new SjukfallPerPatientsPerEnhetConverter(landstingEnhets).convert(casesPerEnhet, filterSettings, null);
        return getResponse(result, csv);
    }

    @GET
    @Path("landstingFilterInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    @PreAuthorize(value = "@protectedLandstingService.hasAccessToLandsting(#request)")
    @PostAuthorize(value = "@protectedLandstingService.userAccess(#request)")
    public Response getLandstingFilterInfo(@Context HttpServletRequest request) {
        final String vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<String> allEnhets = landstingEnhetHandler.getAllEnhetsForVardgivare(vgIdForLoggedInUser);
        final List<Enhet> enhets = enhetManager.getEnhets(allEnhets);
        List<Verksamhet> businesses = Lists.transform(enhets, new Function<Enhet, Verksamhet>() {
            @Override
            public Verksamhet apply(Enhet enhet) {
                return loginServiceUtil.toVerksamhet(enhet);
            }
        });
        final Map<String, Object> result = new HashMap<>();
        result.put("businesses", businesses);
        return Response.ok(result).build();
    }

    private Response getResponse(TableDataReport result, String csv) {
        if (csv == null || csv.isEmpty()) {
            return Response.ok(result).build();
        }
        return CsvConverter.getCsvResponse(result.getTableData(), "export.csv");
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
