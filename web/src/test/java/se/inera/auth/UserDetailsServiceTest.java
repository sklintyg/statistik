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
package se.inera.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.auth.model.User;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.integration.hsa.model.StatisticsPersonInformation;
import se.inera.statistics.integration.hsa.model.UserAuthorization;
import se.inera.statistics.integration.hsa.model.Vardenhet;
import se.inera.statistics.integration.hsa.model.Vardgivare;
import se.inera.statistics.integration.hsa.services.HsaOrganizationsService;
import se.inera.statistics.integration.hsa.services.HsaPersonService;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    private static final String HSA_ID = "TST5565594230-106J";
    private static final Vardenhet VE1_VG1 = new Vardenhet(new HsaIdEnhet("IFV1239877878-103F"), "Enhetsnamn",
        new HsaIdVardgivare("IFV1239877878-0001"));
    private static final Vardenhet VE2_VG1 = new Vardenhet(new HsaIdEnhet("Vardenhet2"), "Enhetsnamn2",
        new HsaIdVardgivare("IFV1239877878-0001"));
    private static final Vardenhet VE3_VG2 = new Vardenhet(new HsaIdEnhet("Vardenhet3"), "Enhetsnamn3", new HsaIdVardgivare("VG2"));
    private static final Vardenhet VE4_VG2 = new Vardenhet(new HsaIdEnhet("Vardenhet4"), "Enhetsnamn4", new HsaIdVardgivare("VG2"));
    private static final String EMPLOYEE_HSA_ID = "TST5565594230-106J";

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private HsaPersonService hsaPersonService;

    @Mock
    private MonitoringLogService monitoringLogService;

    @InjectMocks
    private UserDetailsService service = new UserDetailsService();

    @BeforeEach
    public void setup() {
        setupHsaPersonService();
    }

    @Test
    void vardenhetOnOtherVardgivareAreNotFiltered() {
        auktoriseradeEnheter(VE1_VG1, VE3_VG2, VE4_VG2);
        User user = service.buildUserPrincipal(EMPLOYEE_HSA_ID, LoginMethod.SITHS);
        assertEquals(3, user.getVardenhetList().size());
        assertEquals(VE1_VG1, user.getVardenhetList().get(0));
        assertEquals(VE3_VG2, user.getVardenhetList().get(1));
        assertEquals(VE4_VG2, user.getVardenhetList().get(2));
    }

    @Test
    void hasVgAccessByMultipleEnhets() {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(any(HsaIdUser.class)))
            .thenReturn(new UserAuthorization(Arrays.asList(VE1_VG1, VE2_VG1), Collections.emptyList()));
        User user = service.buildUserPrincipal(EMPLOYEE_HSA_ID, LoginMethod.SITHS);

        assertTrue(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isDelprocessledare());
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isProcessledare());
    }

    @Test
    void hasVgAccessBySystemRole() {
        auktoriseradeEnheter(VE1_VG1, VE3_VG2);
        User user = service.buildUserPrincipal(EMPLOYEE_HSA_ID, LoginMethod.SITHS);
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isDelprocessledare());
        assertTrue(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isProcessledare());
    }

    @Test
    void hasNoVgAccessBySystemRole() {
        final UserAuthorization userAuthorization = new UserAuthorization(Arrays.asList(VE2_VG1, VE3_VG2), Collections.emptyList());
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(any(HsaIdUser.class))).thenReturn(userAuthorization);
        User user = service.buildUserPrincipal(EMPLOYEE_HSA_ID, LoginMethod.SITHS);
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isDelprocessledare());
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isProcessledare());
    }

    @Test
    void testLoadUserByHsaId() {
        auktoriseradeEnheter(VE1_VG1, VE3_VG2);
        User user = service.buildUserPrincipal(HSA_ID, LoginMethod.SITHS);
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isDelprocessledare());
        assertTrue(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isProcessledare());
    }

    @Test
    void shallSetLoginMethod() {
        auktoriseradeEnheter(VE1_VG1, VE3_VG2);
        final var user = service.buildUserPrincipal(HSA_ID, LoginMethod.SITHS);
        assertEquals(LoginMethod.SITHS, user.getLoginMethod());
    }

    private void auktoriseradeEnheter(Vardenhet... enheter) {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(any(HsaIdUser.class)))
            .thenReturn(new UserAuthorization(Arrays.asList(enheter), Arrays.asList("INTYG;Statistik-IFV1239877878-0001")));
        when(hsaOrganizationsService.getVardgivare(any(HsaIdVardgivare.class)))
            .thenReturn(new Vardgivare("IFV1239877878-0001", "Vårdgivare 1"));
    }

    private void setupHsaPersonService() {
        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Arrays.asList(buildPersonInformation()));
    }

    private StatisticsPersonInformation buildPersonInformation() {
        StatisticsPersonInformation pit = new StatisticsPersonInformation();
        pit.setGivenName("Läkar");
        pit.setMiddleAndSurName("Läkarsson");
        return pit;
    }

}