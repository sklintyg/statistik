/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.auth.AuthUtil;
import se.inera.auth.LoginVisibility;
import se.inera.auth.model.User;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.persistance.landsting.Landsting;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class LoginServiceUtilTest {

    @Mock
    private Warehouse warehouse;

    @Mock
    private LandstingEnhetHandler landstingEnhetHandler;

    @Mock
    private LoginVisibility loginVisibility;

    @InjectMocks
    private LoginServiceUtil loginServiceUtilInjected;

    private LoginServiceUtil loginServiceUtil;

    public final Random rand = new Random();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loginServiceUtil = Mockito.spy(loginServiceUtilInjected);
        Mockito.doReturn(true).when(loginServiceUtil).isLoggedIn();
    }

    @Test
    public void testGetSettings() throws Exception {
        // When
        final AppSettings settings = loginServiceUtil.getSettings();

        // Then
        assertEquals(7, settings.getSjukskrivningLengths().size());
        assertEquals("Under 15 dagar", settings.getSjukskrivningLengths().get("GROUP1_0TO14"));
    }

    @Test
    public void testGetLoginInfoSeveralVgsNotProcessledare() throws Exception {
        // Given
        final HsaIdUser userId = new HsaIdUser("testuserid");
        final Vardenhet enhet1 = newVardenhet("e1", "vg1");
        final Vardenhet enhet2 = newVardenhet("e2", "vg1");
        final Vardenhet enhet3 = newVardenhet("e3", "vg2");
        final List<Vardenhet> vardenhetsList = Arrays.asList(enhet1, enhet2, enhet3);
        final User user = new User(userId, "testname", null, vardenhetsList);
        AuthUtil.setUserToSecurityContext(user);

        // When
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();

        // Then
        assertEquals(userId, loginInfo.getHsaId());
        assertEquals(2, loginInfo.getBusinessesForVg(enhet1.getVardgivarId()).size());
        assertEquals(1, loginInfo.getBusinessesForVg(enhet3.getVardgivarId()).size());

        assertEquals(enhet1.getVardgivarId(), loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().getHsaId());
        assertEquals(false, loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().isProcessledare());
        assertEquals(false, loginInfo.getLoginInfoForVg(enhet3.getVardgivarId()).get().isProcessledare());

    }

    @Test
    public void testGetLoginInfoSeveralVgsWhenProcessledare() throws Exception {
        // Given
        final HsaIdUser userId = new HsaIdUser("testuserid");
        final Vardenhet enhet1 = newVardenhet("e1", "vg1");
        final Vardenhet enhet2 = newVardenhet("e2", "vg1");
        final Vardenhet enhet3 = newVardenhet("e3", "vg2");
        final List<Vardenhet> vardenhetsList = Arrays.asList(enhet1, enhet2, enhet3);
        final User user = new User(userId, "testname", Arrays.asList(new Vardgivare("vg2", "Vårdgivare 2")), vardenhetsList);
        AuthUtil.setUserToSecurityContext(user);

        Mockito.when(warehouse.getEnhets(any())).thenAnswer(invocationOnMock -> {
            final HsaIdVardgivare vg = (HsaIdVardgivare) invocationOnMock.getArguments()[0];
            return Arrays.asList(newEnhet(vg), newEnhet(vg), newEnhet(vg), newEnhet(vg));
        });

        Mockito.when(landstingEnhetHandler.getLandsting(any())).thenReturn(buildLandsting());

        // When
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();

        // Then
        assertEquals(userId, loginInfo.getHsaId());
        assertEquals(2, loginInfo.getBusinessesForVg(enhet1.getVardgivarId()).size());
        assertEquals(4, loginInfo.getBusinessesForVg(enhet3.getVardgivarId()).size());

        assertEquals(enhet1.getVardgivarId(), loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().getHsaId());
        assertEquals(false, loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().isProcessledare());
        assertEquals(true, loginInfo.getLoginInfoForVg(enhet3.getVardgivarId()).get().isProcessledare());
    }

    private Optional<Landsting> buildLandsting() {
        Landsting landsting = new Landsting(123L, "Landsting 1", new HsaIdVardgivare("vg1"));
        return Optional.of(landsting);
    }

    private Enhet newEnhet(HsaIdVardgivare vg) {
        return new Enhet(vg, new HsaIdEnhet(String.valueOf(rand.nextInt())), "", "", "", "");
    }

    private Vardenhet newVardenhet(String enhetId, String vgId) {
        return new Vardenhet(new HsaIdEnhet(enhetId), enhetId + "name", new HsaIdVardgivare(vgId));
    }

}
