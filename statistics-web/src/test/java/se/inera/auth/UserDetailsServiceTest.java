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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceTest {
    private static final Vardenhet VE1_VG1 = new Vardenhet("IFV1239877878-103F", "Enhetsnamn", "IFV1239877878-0001");
    private static final Vardenhet VE2_VG1 = new Vardenhet("Vardenhet2", "Enhetsnamn2", "IFV1239877878-0001");
    private static final Vardenhet VE3_VG2 = new Vardenhet("Vardenhet3", "Enhetsnamn3", "VG2");

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @InjectMocks
    private UserDetailsService service = new UserDetailsService();

    private SAMLCredential credential;

    @Before
    public void setup() throws SAXException, UnmarshallingException, ParserConfigurationException, ConfigurationException, IOException {
        Assertion assertion = SakerhetstjanstAssertionTest.getSamlAssertion();

        NameID nameID = new NameIDBuilder().buildObject();
        credential = new SAMLCredential(nameID, assertion, "", "");
    }

    @Test
    public void correctVardenhetIsChosen() throws Exception {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(anyString())).thenReturn(Arrays.asList(VE1_VG1, VE2_VG1));
        User user = (User) service.loadUserBySAML(credential);
        assertEquals("IFV1239877878-103F", user.getValdVardenhet().getId());
    }

    @Test
    public void vardenheterOnOtherVardgivareAreFiltered() throws Exception {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(anyString())).thenReturn(Arrays.asList(VE1_VG1, VE3_VG2));
        User user = (User) service.loadUserBySAML(credential);
        assertEquals(1, user.getVardenhetList().size());
        assertEquals(VE1_VG1, user.getVardenhetList().get(0));
    }

    @Test
    public void hasVgAccessByMultipleEnhets() throws Exception {
        newCredentials("/test-saml-biljett-no-systemroles.xml");
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(anyString())).thenReturn(Arrays.asList(VE1_VG1, VE2_VG1));
        User user = (User) service.loadUserBySAML(credential);
        assertTrue(user.hasVgAccess());
    }

    @Test
    public void hasVgAccessBySystemRole() throws Exception {
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(anyString())).thenReturn(Arrays.asList(VE1_VG1));
        User user = (User) service.loadUserBySAML(credential);
        assertTrue(user.hasVgAccess());
    }

    private void newCredentials(String samlTicketName) {
        Assertion assertion = SakerhetstjanstAssertionTest.getSamlAssertion(samlTicketName);

        NameID nameID = new NameIDBuilder().buildObject();
        credential = new SAMLCredential(nameID, assertion, "", "");
    }

}
