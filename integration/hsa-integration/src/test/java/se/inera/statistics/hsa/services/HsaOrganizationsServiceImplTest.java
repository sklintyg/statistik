/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.hsa.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.common.integration.hsa.client.AuthorizationManagementService;
import se.inera.intyg.common.integration.hsa.stub.Medarbetaruppdrag;
import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
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
    public void testWithSingleMiuStatistik() throws ExternalServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 1));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(1, authorizedEnheter.size());
    }

    @Test
    public void testWithSingleMiuVardOchBehandling() throws ExternalServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.VARD_OCH_BEHANDLING, 1));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(0, authorizedEnheter.size());
    }

    @Test
    public void testThatSingleAuthEnhetIsReturnedWhenHavingTwoMiuOnUnit() throws ExternalServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 2));
        List<Vardenhet> authorizedEnheter = testee.getAuthorizedEnheterForHosPerson(HSA_ID).getVardenhetList();
        assertEquals(1, authorizedEnheter.size());
    }

    @Test
    public void testThatSystemRoleIsSet() throws ExternalServiceCallException {
        when(authorizationManagementService.getAuthorizationsForPerson(anyString(), anyString(), anyString())).thenReturn(buildCredzResponse(Medarbetaruppdrag.STATISTIK, 1));
        UserAuthorization userAuthorization = testee.getAuthorizedEnheterForHosPerson(HSA_ID);
        assertEquals("Statistik;enhet-123", userAuthorization.getSystemRoles().get(0));
    }

    private List<CredentialInformationType> buildCredzResponse(String purpose, int number) {
        List<CredentialInformationType> resp = new ArrayList<>();
        range(0, number).forEach(i -> resp.add(buildCred(purpose)));

        return resp;
    }

    private CredentialInformationType buildCred(String purpose) {
        CredentialInformationType cit = new CredentialInformationType();
        cit.getCommission().add(buildCommission(purpose));
        cit.getHsaSystemRole().add(buildHsaSystemRole());
        return cit;
    }

    private CommissionType buildCommission(String purpose) {
        CommissionType ct = new CommissionType();
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
