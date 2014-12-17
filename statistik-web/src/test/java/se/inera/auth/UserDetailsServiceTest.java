/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.HsaOrganizationsService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceTest {
    private static final Vardenhet VE1_VG1 = new Vardenhet("IFV1239877878-103F", "Enhetsnamn", "IFV1239877878-0001");
    private static final Vardenhet VE2_VG1 = new Vardenhet("Vardenhet2", "Enhetsnamn2", "IFV1239877878-0001");
    private static final Vardenhet VE3_VG2 = new Vardenhet("Vardenhet3", "Enhetsnamn3", "VG2");
    private static final Vardenhet VE4_VG2 = new Vardenhet("Vardenhet4", "Enhetsnamn4", "VG2");

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @InjectMocks
    private UserDetailsService service = new UserDetailsService();

    private SAMLCredential credential;

    @Before
    public void setup() throws SAXException, UnmarshallingException, ParserConfigurationException, ConfigurationException, IOException {
        newCredentials("/test-saml-biljett.xml");
    }

    @Test
    public void correctVardenhetIsChosen() throws Exception {
        auktoriseradeEnheter(VE1_VG1, VE2_VG1);
        User user = (User) service.loadUserBySAML(credential);
        assertEquals("IFV1239877878-103F", user.getValdVardenhet().getId());
        assertEquals(2, user.getVardenhetList().size());
    }

    @Test
    public void vardenhetOnOtherVardgivareAreFiltered() throws Exception {
        auktoriseradeEnheter(VE1_VG1, VE3_VG2, VE4_VG2);
        User user = (User) service.loadUserBySAML(credential);
        assertEquals(1, user.getVardenhetList().size());
        assertEquals(VE1_VG1, user.getVardenhetList().get(0));
    }

    @Test
    public void hasVgAccessByMultipleEnhets() throws Exception {
        newCredentials("/test-saml-biljett-no-systemroles.xml");
        auktoriseradeEnheter(VE1_VG1, VE2_VG1);
        User user = (User) service.loadUserBySAML(credential);
        assertTrue(user.isDelprocessledare());
        assertFalse(user.isProcessledare());
    }

    @Test
    public void hasVgAccessBySystemRole() throws Exception {
        auktoriseradeEnheter(VE1_VG1);
        User user = (User) service.loadUserBySAML(credential);
        assertFalse(user.isDelprocessledare());
        assertTrue(user.isProcessledare());
    }

    private void newCredentials(String samlTicketName) {
        Assertion assertion = SakerhetstjanstAssertionTest.getSamlAssertion(samlTicketName);

        NameID nameID = new NameIDBuilder().buildObject();
        credential = new SAMLCredential(nameID, assertion, "", "");
    }

    private void auktoriseradeEnheter(Vardenhet...enheter) {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(anyString())).thenReturn(Arrays.asList(enheter));
    }

}
