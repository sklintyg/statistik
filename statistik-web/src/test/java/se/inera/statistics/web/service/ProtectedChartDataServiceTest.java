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
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.landsting.LandstingEnhetFileData;
import se.inera.statistics.service.landsting.LandstingEnhetFileDataRow;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.LandstingsVardgivareStatus;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.service.landsting.LandstingEnhetFileParseException;
import se.inera.statistics.web.service.landsting.LandstingFileReader;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProtectedChartDataServiceTest {
    @Mock
    private WarehouseService warehouse;

    @Mock
    private HttpServletRequest request;

    @Mock
    private LoginServiceUtil loginServiceUtil;

    @InjectMocks
    private ProtectedChartDataService chartDataService = new ProtectedChartDataService();

    @Before
    public void init() {
        List<Vardenhet> vardenhets = Arrays.asList(new Vardenhet("verksamhet1", "Närhälsan i Småmåla", "VG1"), new Vardenhet("verksamhet2", "Småmålas akutmottagning", "VG2"));

        User user = new User("hsaId", "name", false, vardenhets.get(0), vardenhets);
        UsernamePasswordAuthenticationToken principal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getDetails()).thenReturn(user);
    }

    @Test
    public void checkDeniedAccessToVerksamhetTest() {
        Mockito.when(loginServiceUtil.getLoginInfo(request)).thenReturn(new LoginInfo());
        boolean result = chartDataService.hasAccessTo(request);
        assertEquals(false, result);
    }

    @Test
    public void checkAllowedAccessToVerksamhetTest() {
        Mockito.when(loginServiceUtil.getLoginInfo(request)).thenReturn(new LoginInfo("", "", null, false, false, false, null, LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE));
        boolean result = chartDataService.hasAccessTo(request);
        assertEquals(true, result);
    }

}
