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
package se.inera.statistics.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.inera.auth.model.User;
import se.inera.auth.model.UserAccessLevel;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.region.RegionsVardgivareStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginInfoTest {

    @Test
    public void testGetLoginInfoForVg() {
        // Given
        final LoginInfoVg vg1 = new LoginInfoVg(new HsaIdVardgivare("vgid1"), "vg1", RegionsVardgivareStatus.REGIONSVARDGIVARE_WITHOUT_UPLOAD, new UserAccessLevel(true, 2));
        final LoginInfoVg vg2 = new LoginInfoVg(new HsaIdVardgivare("vgid2"), "vg2", RegionsVardgivareStatus.REGIONSVARDGIVARE_WITHOUT_UPLOAD, new UserAccessLevel(true, 2));
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser("testid"), "testname", null, new ArrayList<>(Arrays.asList(vg1, vg2)), new UserSettingsDTO(), "FAKE");

        // Then
        assertEquals(vg1, loginInfo.getLoginInfoForVg(vg1.getHsaId()).get());
        assertEquals(vg2, loginInfo.getLoginInfoForVg(vg2.getHsaId()).get());
    }

    @Test
    public void testGetLoginInfoForVgNullSafe() {
        // Given
        final LoginInfo loginInfo = new LoginInfo(null, null, null, null, null, null);

        // When
        assertEquals("", loginInfo.getName());
        assertEquals(HsaIdUser.empty().getId(), loginInfo.getHsaId().getId());
        assertTrue(loginInfo.getVgs().isEmpty());
        assertTrue(loginInfo.getBusinessesForVg(HsaIdVardgivare.empty()).isEmpty());
        assertTrue(loginInfo.getBusinessesForVg(null).isEmpty());
        assertFalse(loginInfo.getLoginInfoForVg(HsaIdVardgivare.empty()).isPresent());
        assertFalse(loginInfo.getLoginInfoForVg(null).isPresent());
        assertFalse(loginInfo.isLoggedIn());

        // Then no exception has been thrown
    }

    @Test
    public void testLoginInfoIsImmutable() {
        // Given
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser("testid"), "testname", new ArrayList<>(), new ArrayList<>(),
                new UserSettingsDTO(), "FAKE");

        // When
        try {
            loginInfo.getVgs().add(LoginInfoVg.empty());
        } catch (UnsupportedOperationException ignored) {
        }

        // Then
        assertEquals(0, loginInfo.getVgs().size());
    }

    private User createUser(String id, String name) {
        return new User(new HsaIdUser(id), name, null, null);
    }

    @Test
    public void testGetBusinessesForVg() {
        // Given
        final Verksamhet e1 = createVerksamhet("e1", "vg1");
        final Verksamhet e2 = createVerksamhet("e2", "vg1");
        final Verksamhet e3 = createVerksamhet("e3", "vg2");
        final LoginInfo loginInfo = new LoginInfo(new HsaIdUser("testid"), "testname", new ArrayList<>(Arrays.asList(e1, e2, e3)), null,
                null, "FAKE");

        // When
        final List<Verksamhet> businessesForVg1 = loginInfo.getBusinessesForVg(e1.getVardgivarId());
        final List<Verksamhet> businessesForVg2 = loginInfo.getBusinessesForVg(e3.getVardgivarId());

        // Then
        assertEquals(2, businessesForVg1.size());
        assertTrue(businessesForVg1.contains(e1));
        assertTrue(businessesForVg1.contains(e2));

        assertEquals(1, businessesForVg2.size());
        assertTrue(businessesForVg2.contains(e3));
    }

    private Verksamhet createVerksamhet(String enhetId, String vgId) {
        return new Verksamhet(new HsaIdEnhet(enhetId), "", new HsaIdVardgivare(vgId), "", "", "", "", "", null);
    }
}
