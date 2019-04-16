/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.google.common.collect.Lists;

import se.inera.auth.model.User;
import se.inera.auth.model.UserAccessLevel;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.landsting.LandstingsVardgivareStatus;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.UserSettingsDTO;
import se.inera.statistics.web.service.FilterHandler;
import se.inera.statistics.web.service.FilterHashHandler;
import se.inera.statistics.web.service.LoginServiceUtil;
import se.inera.statistics.web.service.WarehouseService;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@RunWith(MockitoJUnitRunner.class)
public class ProtectedChartDataServiceTest {
    @Mock
    private WarehouseService warehouse;

    @Mock
    private HttpServletRequest request;

    @Mock
    private LoginServiceUtil loginServiceUtil;

    @Mock
    private MonitoringLogService monitoringLogService;

    @Mock
    private FilterHandler filterHandler;

    @Mock
    private FilterHashHandler filterHashHandler;

    @InjectMocks
    private ProtectedChartDataService chartDataService = new ProtectedChartDataService();

    @Before
    public void init() {
        final Vardenhet vardenhet1 = new Vardenhet(new HsaIdEnhet("verksamhet1"), "Närhälsan i Småmåla", new HsaIdVardgivare("VG1"));
        final Vardenhet vardenhet2 = new Vardenhet(new HsaIdEnhet("verksamhet2"), "Småmålas akutmottagning", new HsaIdVardgivare("VG2"));
        List<Vardenhet> vardenhets = Arrays.asList(vardenhet1, vardenhet2);

        User user = new User(new HsaIdUser("hsaId"), "name", Collections.emptyList(), vardenhets);
        UsernamePasswordAuthenticationToken principal = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getDetails()).thenReturn(user);
    }

    @Test
    public void checkDeniedAccessToVerksamhetTest() {
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(new LoginInfo());
        boolean result = chartDataService.hasAccessTo(request);
        assertEquals(false, result);
    }

    @Test
    public void checkAllowedAccessToVerksamhetTest() {
        // Given
        final HsaIdVardgivare testvg = new HsaIdVardgivare("testvg");
        final List<LoginInfoVg> loginInfoVgs = Collections.singletonList(
                new LoginInfoVg(testvg, "", LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITHOUT_UPLOAD, new UserAccessLevel(false, 2)));
        Mockito.when(loginServiceUtil.getLoginInfo())
                .thenReturn(new LoginInfo(new HsaIdUser("testid"), "", Lists.newArrayList(), loginInfoVgs, new UserSettingsDTO(), "FAKE"));
        Mockito.when(loginServiceUtil.getSelectedVgIdForLoggedInUser(request)).thenReturn(testvg);

        // When
        boolean result = chartDataService.hasAccessTo(request);

        // Then
        assertEquals(true, result);
    }

    @Test
    public void userAccessShouldLog() {
        Mockito.when(loginServiceUtil.getLoginInfo()).thenReturn(
                new LoginInfo(new HsaIdUser(""), "", Lists.newArrayList(), Lists.newArrayList(), new UserSettingsDTO(), "FAKE"));
        chartDataService.userAccess(request);
    }

    /**
     * For INTYG-3446: This test case asserts that if the user has Medarbetaruppdrag on one VG1, and _only_ a proper
     * systemRole on VG2, then the user should be allowed access to both.
     */
    @Test
    public void checkUserWithSystemRoleOnlyHasAccessToEntireVg() {
        final HsaIdVardgivare testvg = new HsaIdVardgivare("testvg");
        final HsaIdVardgivare testvg2 = new HsaIdVardgivare("testvg-2");
        final List<LoginInfoVg> loginInfoVgs = Arrays.asList(
                new LoginInfoVg(testvg, "", LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITHOUT_UPLOAD, new UserAccessLevel(false, 2)),
                new LoginInfoVg(testvg2, "", LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITH_UPLOAD, new UserAccessLevel(true, 0)));

        Mockito.when(loginServiceUtil.getLoginInfo())
                .thenReturn(new LoginInfo(new HsaIdUser("testid"), "", Lists.newArrayList(), loginInfoVgs, new UserSettingsDTO(), "FAKE"));
        Mockito.when(loginServiceUtil.getSelectedVgIdForLoggedInUser(request)).thenReturn(testvg2);

        // When
        boolean result = chartDataService.hasAccessTo(request);

        // Then
        assertEquals(true, result);
    }

}
