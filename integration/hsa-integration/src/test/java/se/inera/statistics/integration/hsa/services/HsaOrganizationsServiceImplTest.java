/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.integration.hsa.services;

import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.model.Commission;
import se.inera.intyg.infra.integration.hsatk.model.HsaSystemRole;
import se.inera.intyg.infra.integration.hsatk.services.HsatkAuthorizationManagementService;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.Medarbetaruppdrag;
import se.inera.statistics.integration.hsa.model.UserAuthorization;
import se.inera.statistics.integration.hsa.model.Vardenhet;

/**
 * Created by eriklupander on 2016-04-13.
 */
@RunWith(MockitoJUnitRunner.class)
public class HsaOrganizationsServiceImplTest {

    private static final HsaIdUser HSA_ID = new HsaIdUser("hsa-123");

    @Mock
    HsatkAuthorizationManagementService authorizationManagementService;

    @InjectMocks
    private HsaOrganizationsServiceImpl testee;

    @Test
    public void testWithSingleMiuStatistik() throws HsaServiceCallException {
        when(authorizationManagementService.getCredentialInformationForPerson(anyString(), anyString(), anyString()))
            .thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 1, 1));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(1, authorizedEnheter.size());
    }

    @Test
    public void testWithSingleMiuVardOchBehandling() throws HsaServiceCallException {
        when(authorizationManagementService.getCredentialInformationForPerson(anyString(), anyString(), anyString()))
            .thenReturn(buildCredzResponse(Medarbetaruppdrag.VARD_OCH_BEHANDLING, 1, 1));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(0, authorizedEnheter.size());
    }

    @Test
    public void testThatSingleAuthEnhetIsReturnedWhenHavingTwoMiuOnUnit() throws HsaServiceCallException {
        when(authorizationManagementService.getCredentialInformationForPerson(anyString(), anyString(), anyString()))
            .thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 2, 0));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(1, authorizedEnheter.size());
    }

    @Test
    public void testThatSystemRoleIsSet() throws HsaServiceCallException {
        when(authorizationManagementService.getCredentialInformationForPerson(anyString(), anyString(), anyString()))
            .thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 1, 1));
        UserAuthorization userAuthorization = testee.getAuthorizedEnheterForHosPerson(HSA_ID);
        assertEquals("Statistik;enhet-123", userAuthorization.getSystemRoles().get(0));
    }

    @Test
    public void testThatVEAreSortedByVEId() throws HsaServiceCallException {
        List<CredentialInformation> credzResp = buildCredzResponse(Medarbetaruppdrag.STATISTIK, 3, 3);
        when(authorizationManagementService.getCredentialInformationForPerson(anyString(), anyString(), anyString())).thenReturn(credzResp);
        UserAuthorization userAuthorization = testee.getAuthorizedEnheterForHosPerson(HSA_ID);
        System.out.println(userAuthorization.getVardenhetList());
        assertEquals("ENHET-1", userAuthorization.getVardenhetList().get(0).getId().getId());
        assertEquals("ENHET-2", userAuthorization.getVardenhetList().get(1).getId().getId());
        assertEquals("ENHET-3", userAuthorization.getVardenhetList().get(2).getId().getId());
    }

    private List<CredentialInformation> buildCredzResponse(String purpose, int number, int maxNumber) {
        List<CredentialInformation> resp = new ArrayList<>();
        range(0, number).forEach(i -> resp.add(buildCred(purpose, maxNumber - i)));

        return resp;
    }

    private CredentialInformation buildCred(String purpose, int index) {
        CredentialInformation cit = new CredentialInformation();
        cit.getCommission().add(buildCommission(purpose, index));
        cit.getHsaSystemRole().add(buildHsaSystemRole());
        return cit;
    }

    private Commission buildCommission(String purpose, int index) {
        // If index less that 0 we set zero, little trick to make units with same ID...
        if (index < 0) {
            index = 0;
        }
        Commission ct = new Commission();
        ct.setHealthCareUnitHsaId("enhet-" + index);
        ct.setCommissionPurpose(purpose);
        return ct;
    }

    private HsaSystemRole buildHsaSystemRole() {
        HsaSystemRole hsaSystemRoleType = new HsaSystemRole();
        hsaSystemRoleType.setSystemId("Statistik");
        hsaSystemRoleType.setRole("enhet-123");
        return hsaSystemRoleType;
    }
}
