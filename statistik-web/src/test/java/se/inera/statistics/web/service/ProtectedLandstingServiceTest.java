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

import com.google.common.base.Optional;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.landsting.LandstingEnhetFileData;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.LandstingsVardgivareStatus;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdate;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateOperation;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.service.landsting.LandstingEnhetFileParseException;
import se.inera.statistics.web.service.landsting.LandstingFileGenerationException;
import se.inera.statistics.web.service.landsting.LandstingFileReader;
import se.inera.statistics.web.service.landsting.LandstingFileWriter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProtectedLandstingServiceTest {

    @Mock
    private WarehouseService warehouse;

    @Mock
    private HttpServletRequest request;

    @Mock
    private LoginServiceUtil loginServiceUtil;

    @Mock
    private LandstingEnhetHandler landstingEnhetHandler;

    @Mock
    private LandstingFileReader landstingFileReader;

    @Mock
    private LandstingFileWriter landstingFileWriter;

    @Mock
    private EnhetManager enhetManager;

    @InjectMocks
    private ProtectedLandstingService chartDataService = new ProtectedLandstingService();

    @Before
    public void init() {
        List<Vardenhet> vardenhets = Arrays.asList(new Vardenhet(new HsaIdEnhet("verksamhet1"), "Närhälsan i Småmåla", new HsaIdVardgivare("VG1")), new Vardenhet(new HsaIdEnhet("verksamhet2"), "Småmålas akutmottagning", new HsaIdVardgivare("VG2")));

        User user = new User(new HsaIdUser("hsaId"), "name", false, vardenhets.get(0), vardenhets);
        UsernamePasswordAuthenticationToken principal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getDetails()).thenReturn(user);
    }

    @Test
    public void testFileUploadWhenUserNotProcessledareShouldFail() throws Exception {
        //Given
        final MultipartBody mb = Mockito.mock(MultipartBody.class);
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(loginServiceUtil.getLoginInfo(req)).thenReturn(new LoginInfo(new HsaIdUser(""), "", null, false, false, false, null, LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE));

        //When
        final Response response = chartDataService.fileupload(req, mb);

        //Then
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        Mockito.verify(landstingEnhetHandler, times(0)).update(any(LandstingEnhetFileData.class));
    }

    @Test
    public void testFileUploadWhenFileParseFailsThenNoUpdateShouldBeDone() throws Exception {
        //Given
        final MultipartBody mb = Mockito.mock(MultipartBody.class);
        final Attachment attachment = Mockito.mock(Attachment.class);
        Mockito.when(mb.getAttachment(anyString())).thenReturn(attachment);
        final DataHandler dh = Mockito.mock(DataHandler.class);
        Mockito.when(attachment.getDataHandler()).thenReturn(dh);
        final DataSource ds = Mockito.mock(DataSource.class);
        Mockito.when(dh.getDataSource()).thenReturn(ds);
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(loginServiceUtil.getLoginInfo(req)).thenReturn(new LoginInfo(new HsaIdUser(""), "", null, false, false, true, null, LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE));
        Mockito.when(landstingFileReader.readExcelData(any(DataSource.class))).thenThrow(new LandstingEnhetFileParseException(""));

        //When
        final Response response = chartDataService.fileupload(req, mb);

        //Then
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        Mockito.verify(landstingEnhetHandler, times(0)).update(any(LandstingEnhetFileData.class));
    }

    @Test
    public void testFileUploadIsReturningAnHtmlPageWithCorrectMessageWhenRequired() throws Exception {
        //Given
        final MultipartBody mb = Mockito.mock(MultipartBody.class);
        final Attachment attachment = Mockito.mock(Attachment.class);
        Mockito.when(mb.getAttachment(anyString())).thenReturn(attachment);
        Mockito.when(mb.getAttachmentObject(anyString(), any(Class.class))).thenReturn("true");
        final DataHandler dh = Mockito.mock(DataHandler.class);
        Mockito.when(attachment.getDataHandler()).thenReturn(dh);
        final DataSource ds = Mockito.mock(DataSource.class);
        Mockito.when(ds.getName()).thenReturn("testfilename");
        Mockito.when(dh.getDataSource()).thenReturn(ds);
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(loginServiceUtil.getLoginInfo(req)).thenReturn(new LoginInfo(new HsaIdUser(""), "", null, false, false, true, null, LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE));
        final String msg = "This is a test message";
        Mockito.when(landstingFileReader.readExcelData(any(DataSource.class))).thenThrow(new LandstingEnhetFileParseException(msg));

        //When
        final Response response = chartDataService.fileupload(req, mb);

        //Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).startsWith("<html>"));
        assertTrue(((String) response.getEntity()).contains(msg));
        Mockito.verify(landstingEnhetHandler, times(0)).update(any(LandstingEnhetFileData.class));
    }

    @Test
    public void testFileUploadUpdateIsUsingResultFromFileParsingAndCorrectVgId() throws Exception {
        //Given
        final MultipartBody mb = Mockito.mock(MultipartBody.class);
        final Attachment attachment = Mockito.mock(Attachment.class);
        Mockito.when(mb.getAttachment(anyString())).thenReturn(attachment);
        final DataHandler dh = Mockito.mock(DataHandler.class);
        Mockito.when(attachment.getDataHandler()).thenReturn(dh);
        final DataSource ds = Mockito.mock(DataSource.class);
        Mockito.when(ds.getName()).thenReturn("testfilename");
        Mockito.when(dh.getDataSource()).thenReturn(ds);
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final Verksamhet verksamhet = Mockito.mock(Verksamhet.class);
        final HsaIdVardgivare testVgId = new HsaIdVardgivare("TestVgId");
        Mockito.when(verksamhet.getVardgivarId()).thenReturn(testVgId);
        Mockito.when(loginServiceUtil.getLoginInfo(req)).thenReturn(new LoginInfo(new HsaIdUser(""), "", verksamhet, false, false, true, null, LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE));
        final ArrayList<LandstingEnhetFileDataRow> parseResult = new ArrayList<>();
        Mockito.when(landstingFileReader.readExcelData(any(DataSource.class))).thenReturn(parseResult);

        //When
        final Response response = chartDataService.fileupload(req, mb);

        //Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final ArgumentCaptor<LandstingEnhetFileData> captor = ArgumentCaptor.forClass(LandstingEnhetFileData.class);
        Mockito.verify(landstingEnhetHandler, times(1)).update(captor.capture());
        assertEquals(parseResult, captor.getValue().getRows());
        assertEquals(testVgId, captor.getValue().getVgId());
    }

    @Test
    public void testGetPrepopulatedLandstingFileHappyPath() throws Exception {
        //Given
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        final HsaIdVardgivare vgid = new HsaIdVardgivare("VgidTest");
        Mockito.when(loginServiceUtil.getSelectedVgIdForLoggedInUser(req)).thenReturn(vgid);

        final ArrayList<Enhet> enhets = new ArrayList<>();
        Mockito.when(enhetManager.getAllEnhetsForVardgivareId(vgid)).thenReturn(enhets);

        final ByteArrayOutputStream outputStream = Mockito.mock(ByteArrayOutputStream.class);
        Mockito.when(landstingFileWriter.generateExcelFile(enhets)).thenReturn(outputStream);

        final String resultContent = "TestResultContent";
        Mockito.when(outputStream.toByteArray()).thenReturn(resultContent.getBytes());

        //When
        final Response response = chartDataService.getPrepopulatedLandstingFile(req);

        //Then
        assertArrayEquals(resultContent.getBytes(), (byte[]) response.getEntity());
        assertEquals("attachment; filename=\"" + vgid + "_landsting.xlsx\"", response.getHeaderString("Content-Disposition"));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE, response.getMediaType());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetPrepopulatedLandstingFileGenerationFailure() throws Exception {
        //Given
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        final HsaIdVardgivare vgid = new HsaIdVardgivare("VgidTest");
        Mockito.when(loginServiceUtil.getSelectedVgIdForLoggedInUser(req)).thenReturn(vgid);

        final ArrayList<Enhet> enhets = new ArrayList<>();
        Mockito.when(enhetManager.getAllEnhetsForVardgivareId(vgid)).thenReturn(enhets);

        Mockito.when(landstingFileWriter.generateExcelFile(enhets)).thenThrow(new LandstingFileGenerationException());

        //When
        final Response response = chartDataService.getPrepopulatedLandstingFile(req);

        //Then
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetLastLandstingUpdateInfo() throws Exception {
        //Given
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        final HsaIdVardgivare vgid = new HsaIdVardgivare("VgidTest");
        Mockito.when(loginServiceUtil.getSelectedVgIdForLoggedInUser(req)).thenReturn(vgid);

        final LandstingEnhetUpdate landstingEnhetUpdate = new LandstingEnhetUpdate(123L, "Test Name", new HsaIdUser("TESTHSAID"), new Timestamp(5L), "TestFile.xls", LandstingEnhetUpdateOperation.Update);
        Mockito.when(landstingEnhetHandler.getLastUpdateInfo(vgid)).thenReturn(Optional.of(landstingEnhetUpdate));

        final ArrayList<LandstingEnhet> landstingEnhets = new ArrayList<>();
        landstingEnhets.add(new LandstingEnhet(3L, new HsaIdEnhet("HSAID3"), 73));
        landstingEnhets.add(new LandstingEnhet(9L, new HsaIdEnhet("HSAID9"), 79));
        Mockito.when(landstingEnhetHandler.getAllLandstingEnhetsForVardgivare(vgid)).thenReturn(landstingEnhets);

        //When
        final Response response = chartDataService.getLastLandstingUpdateInfo(req);

        //Then
        final HashMap<String, Object> entity = (HashMap<String, Object>) response.getEntity();

        final List<String> parsedRows = (List<String>) entity.get("parsedRows");
        assertEquals(2, parsedRows.size());
        assertEquals("HSA-id: HSAID3 -> Listade patienter: 73", parsedRows.get(0));
        assertEquals("HSA-id: HSAID9 -> Listade patienter: 79", parsedRows.get(1));

        final String infoMessage = (String) entity.get("infoMessage");
        assertTrue(infoMessage.contains("TestFile.xls"));
        assertTrue(infoMessage.contains("Test Name"));
        assertTrue(infoMessage.contains("TESTHSAID"));
    }

    @Test
    public void testClearLandstingEnhets() throws Exception {
        //Given
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        final Verksamhet verksamhet = Mockito.mock(Verksamhet.class);
        final HsaIdVardgivare vgId = new HsaIdVardgivare("testvgid");
        Mockito.when(verksamhet.getVardgivarId()).thenReturn(vgId);
        final HsaIdUser hsaId = new HsaIdUser("testhsaid");
        final String name = "test name";
        Mockito.when(loginServiceUtil.getLoginInfo(req)).thenReturn(new LoginInfo(hsaId, name, verksamhet, false, false, false, null, LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE));

        final HsaIdVardgivare vgid = new HsaIdVardgivare("VgidTest");
        Mockito.when(loginServiceUtil.getSelectedVgIdForLoggedInUser(req)).thenReturn(vgid);

        //When
        final Response response = chartDataService.clearLandstingEnhets(req);

        //Then
        assertEquals(200, response.getStatus());
        verify(landstingEnhetHandler, times(1)).clear(vgId, name, hsaId);
    }

}
