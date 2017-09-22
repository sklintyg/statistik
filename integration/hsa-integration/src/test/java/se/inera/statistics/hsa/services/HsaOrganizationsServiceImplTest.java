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
package se.inera.statistics.hsa.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.client.AuthorizationManagementService;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsa.stub.Medarbetaruppdrag;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.Vardenhet;
import se.riv.infrastructure.directory.v1.CommissionType;
import se.riv.infrastructure.directory.v1.CredentialInformationType;
import se.riv.infrastructure.directory.v1.HsaSystemRoleType;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2016-04-13.
 */
@RunWith(MockitoJUnitRunner.class)
public class HsaOrganizationsServiceImplTest {

    private static final HsaIdUser HSA_ID = new HsaIdUser("hsa-123");

    @Mock
    AuthorizationManagementService authorizationManagementService;

    @InjectMocks
    private HsaOrganizationsServiceImpl testee;
    
    @Test
    public void testWithSingleMiuStatistik() throws HsaServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 1, 1));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(1, authorizedEnheter.size());
    }

    @Test
    public void testWithSingleMiuVardOchBehandling() throws HsaServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.VARD_OCH_BEHANDLING, 1, 1));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(0, authorizedEnheter.size());
    }

    @Test
    public void testThatSingleAuthEnhetIsReturnedWhenHavingTwoMiuOnUnit() throws HsaServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 2, 0));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(1, authorizedEnheter.size());
    }

    @Test
    public void testThatSystemRoleIsSet() throws HsaServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 1, 1));
        UserAuthorization userAuthorization = testee.getAuthorizedEnheterForHosPerson(HSA_ID);
        assertEquals("Statistik;enhet-123", userAuthorization.getSystemRoles().get(0));
    }

    @Test
    public void testThatVEAreSortedByVEId() throws HsaServiceCallException {
        List<CredentialInformationType> credzResp = buildCredzResponse(Medarbetaruppdrag.STATISTIK, 3, 3);
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(credzResp);
        UserAuthorization userAuthorization = testee.getAuthorizedEnheterForHosPerson(HSA_ID);
        System.out.println(userAuthorization.getVardenhetList());
        assertEquals("ENHET-1", userAuthorization.getVardenhetList().get(0).getId().getId());
        assertEquals("ENHET-2", userAuthorization.getVardenhetList().get(1).getId().getId());
        assertEquals("ENHET-3", userAuthorization.getVardenhetList().get(2).getId().getId());
    }

    private List<CredentialInformationType> buildCredzResponse(String purpose, int number, int maxNumber) {
        List<CredentialInformationType> resp = new ArrayList<>();
        range(0, number).forEach(i -> resp.add(buildCred(purpose, maxNumber - i)));

        return resp;
    }

    private CredentialInformationType buildCred(String purpose, int index) {
        CredentialInformationType cit = new CredentialInformationType();
        cit.getCommission().add(buildCommission(purpose, index));
        cit.getHsaSystemRole().add(buildHsaSystemRole());
        return cit;
    }

    private CommissionType buildCommission(String purpose, int index) {
        // If index less that 0 we set zero, little trick to make units with same ID...
        if (index < 0) {
            index = 0;
        }
        CommissionType ct = new CommissionType();
        ct.setHealthCareUnitHsaId("enhet-" + index);
        ct.setCommissionPurpose(purpose);
        return ct;
    }

    private HsaSystemRoleType buildHsaSystemRole() {
        HsaSystemRoleType hsaSystemRoleType = new HsaSystemRoleType();
        hsaSystemRoleType.setSystemId("Statistik");
        hsaSystemRoleType.setRole("enhet-123");
        return hsaSystemRoleType;
    }
}
