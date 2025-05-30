/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.auth.AuthUtil;
import se.inera.auth.LoginMethod;
import se.inera.auth.LoginVisibility;
import se.inera.auth.model.User;
import se.inera.intyg.infra.integration.ia.services.IABannerService;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.integration.hsa.model.Vardenhet;
import se.inera.statistics.integration.hsa.model.Vardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.region.RegionEnhetHandler;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.user.UserSettingsManager;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.StaticData;
import se.inera.statistics.web.util.VersionUtil;

public class LoginServiceUtilTest {

    @Mock
    private Warehouse warehouse;

    @Mock
    private RegionEnhetHandler regionEnhetHandler;

    @Mock
    private LoginVisibility loginVisibility;

    @Mock
    private UserSettingsManager userSettingsManager;

    @Mock
    private Icd10 icd10;

    @Mock
    private IABannerService iaBannerService;

    @Mock
    private VersionUtil versionUtil;

    @InjectMocks
    private LoginServiceUtil loginServiceUtilInjected;

    private LoginServiceUtil loginServiceUtil;

    private final Random rand = new Random();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginServiceUtil = Mockito.spy(loginServiceUtilInjected);
        Mockito.doReturn(true).when(loginServiceUtil).isLoggedIn();
    }

    @Test
    public void testGetStaticData() {
        // When
        final StaticData staticData = loginServiceUtil.getStaticData();

        // Then
        assertEquals(8, staticData.getSjukskrivningLengths().size());
        assertEquals("Under 15 dagar", staticData.getSjukskrivningLengths().get("GROUP1_0TO14"));
    }

    @Test
    public void testGetLoginInfoSeveralVgsNotProcessledare() {
        // Given
        final HsaIdUser userId = new HsaIdUser("testuserid");
        final Vardenhet enhet1 = newVardenhet("e1", "vg1");
        final Vardenhet enhet2 = newVardenhet("e2", "vg1");
        final Vardenhet enhet3 = newVardenhet("e3", "vg2");
        final List<Vardenhet> vardenhetsList = Arrays.asList(enhet1, enhet2, enhet3);
        final User user = new User(userId, "testname", null, vardenhetsList, LoginMethod.SITHS);
        AuthUtil.setUserToSecurityContext(user);

        // When
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();

        // Then
        assertEquals(userId, loginInfo.getHsaId());
        assertEquals(2, loginInfo.getBusinessesForVg(enhet1.getVardgivarId()).size());
        assertEquals(1, loginInfo.getBusinessesForVg(enhet3.getVardgivarId()).size());

        assertEquals(enhet1.getVardgivarId(), loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().getHsaId());
        assertFalse(loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().isProcessledare());
        assertFalse(loginInfo.getLoginInfoForVg(enhet3.getVardgivarId()).get().isProcessledare());

    }

    @Test
    public void testGetLoginInfoSeveralVgsWhenProcessledare() {
        // Given
        final HsaIdUser userId = new HsaIdUser("testuserid");
        final Vardenhet enhet1 = newVardenhet("e1", "vg1");
        final Vardenhet enhet2 = newVardenhet("e2", "vg1");
        final Vardenhet enhet3 = newVardenhet("e3", "vg2");
        final List<Vardenhet> vardenhetsList = Arrays.asList(enhet1, enhet2, enhet3);
        final User user = new User(userId, "testname", Arrays.asList(new Vardgivare("vg2", "Vårdgivare 2")), vardenhetsList,
            LoginMethod.SITHS);
        AuthUtil.setUserToSecurityContext(user);

        when(warehouse.getEnhets(any())).thenAnswer(invocationOnMock -> {
            final HsaIdVardgivare vg = (HsaIdVardgivare) invocationOnMock.getArguments()[0];
            return Arrays.asList(newEnhet(vg), newEnhet(vg), newEnhet(vg), newEnhet(vg));
        });

        // When
        final LoginInfo loginInfo = loginServiceUtil.getLoginInfo();

        // Then
        assertEquals(userId, loginInfo.getHsaId());
        assertEquals(2, loginInfo.getBusinessesForVg(enhet1.getVardgivarId()).size());
        assertEquals(4, loginInfo.getBusinessesForVg(enhet3.getVardgivarId()).size());

        assertEquals(enhet1.getVardgivarId(), loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().getHsaId());
        assertFalse(loginInfo.getLoginInfoForVg(enhet1.getVardgivarId()).get().isProcessledare());
        assertTrue(loginInfo.getLoginInfoForVg(enhet3.getVardgivarId()).get().isProcessledare());
    }

    private Enhet newEnhet(HsaIdVardgivare vg) {
        return new Enhet(vg, new HsaIdEnhet(String.valueOf(rand.nextInt())), "", "", "", "", "veid");
    }

    private Vardenhet newVardenhet(String enhetId, String vgId) {
        return new Vardenhet(new HsaIdEnhet(enhetId), enhetId + "name", new HsaIdVardgivare(vgId));
    }

    @Test
    public void testGetAppSettingsContainsCorrectProjectVersion() {
        // Given
        String projectVersion = "TestversionXXX";
        when(versionUtil.getProjectVersion()).thenReturn(projectVersion);

        // When
        AppSettings settings = loginServiceUtil.getSettings();

        // Then
        assertEquals(projectVersion, settings.getProjectVersion());
    }

}