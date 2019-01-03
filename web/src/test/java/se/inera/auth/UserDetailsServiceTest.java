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
package se.inera.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.security.saml.SAMLCredential;
import org.xml.sax.SAXException;
import se.inera.auth.model.User;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.services.HsaPersonService;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.HsaOrganizationsService;
import se.inera.statistics.hsa.services.UserAuthorization;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceTest {
    private static final Vardenhet VE1_VG1 = new Vardenhet(new HsaIdEnhet("IFV1239877878-103F"), "Enhetsnamn", new HsaIdVardgivare("IFV1239877878-0001"));
    private static final Vardenhet VE2_VG1 = new Vardenhet(new HsaIdEnhet("Vardenhet2"), "Enhetsnamn2", new HsaIdVardgivare("IFV1239877878-0001"));
    private static final Vardenhet VE3_VG2 = new Vardenhet(new HsaIdEnhet("Vardenhet3"), "Enhetsnamn3", new HsaIdVardgivare("VG2"));
    private static final Vardenhet VE4_VG2 = new Vardenhet(new HsaIdEnhet("Vardenhet4"), "Enhetsnamn4", new HsaIdVardgivare("VG2"));

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private HsaPersonService hsaPersonService;

    @Mock
    private MonitoringLogService monitoringLogService;

    @InjectMocks
    private UserDetailsService service = new UserDetailsService();

    private SAMLCredential credential;

    @Before
    public void setup() throws SAXException, UnmarshallingException, ParserConfigurationException, ConfigurationException, IOException {
        newCredentials("/test-saml-biljett-uppdragslos.xml");
        setupHsaPersonService();
    }

    @Test
    public void vardenhetOnOtherVardgivareAreNotFiltered() throws Exception {
        auktoriseradeEnheter(VE1_VG1, VE3_VG2, VE4_VG2);
        User user = (User) service.loadUserBySAML(credential);
        assertEquals(3, user.getVardenhetList().size());
        assertEquals(VE1_VG1, user.getVardenhetList().get(0));
        assertEquals(VE3_VG2, user.getVardenhetList().get(1));
        assertEquals(VE4_VG2, user.getVardenhetList().get(2));
    }

    @Test
    public void hasVgAccessByMultipleEnhets() throws Exception {
        newCredentials("/test-saml-biljett-uppdragslos.xml");
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(any(HsaIdUser.class))).thenReturn(new UserAuthorization(Arrays.asList(VE1_VG1, VE2_VG1), Collections.emptyList()));
        User user = (User) service.loadUserBySAML(credential);
        assertTrue(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isDelprocessledare());
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isProcessledare());
    }

    @Test
    public void hasVgAccessBySystemRole() throws Exception {
        auktoriseradeEnheter(VE1_VG1, VE3_VG2);
        User user = (User) service.loadUserBySAML(credential);
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isDelprocessledare());
        assertTrue(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isProcessledare());
    }

    @Test
    public void hasNoVgAccessBySystemRole() throws Exception {
        final UserAuthorization userAuthorization = new UserAuthorization(Arrays.asList(VE2_VG1, VE3_VG2), Collections.emptyList());
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(any(HsaIdUser.class))).thenReturn(userAuthorization);
        User user = (User) service.loadUserBySAML(credential);
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isDelprocessledare());
        assertFalse(user.getUserAccessLevelForVg(VE1_VG1.getVardgivarId()).isProcessledare());
    }

    private void newCredentials(String samlTicketName) {
        Assertion assertion = SakerhetstjanstAssertionTest.getSamlAssertion(samlTicketName);

        NameID nameID = new NameIDBuilder().buildObject();
        credential = new SAMLCredential(nameID, assertion, "", "");
    }

    private void auktoriseradeEnheter(Vardenhet...enheter) {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(any(HsaIdUser.class))).thenReturn(new UserAuthorization(Arrays.asList(enheter), Arrays.asList("INTYG;Statistik-IFV1239877878-0001")));
        when(hsaOrganizationsService.getVardgivare(any(HsaIdVardgivare.class))).thenReturn(new Vardgivare("IFV1239877878-0001", "Vårdgivare 1"));
    }

    private void setupHsaPersonService() {
        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Arrays.asList(buildPersonInformation()));
    }

    private PersonInformationType buildPersonInformation() {
        PersonInformationType pit = new PersonInformationType();
        pit.setGivenName("Läkar");
        pit.setMiddleAndSurName("Läkarsson");
        return pit;
    }

}
