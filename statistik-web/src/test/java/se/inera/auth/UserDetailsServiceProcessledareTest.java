/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
import org.mockito.Mockito;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.HsaOrganizationsService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for STATISTIK-961
 */
public class UserDetailsServiceProcessledareTest {

    private UserDetailsService userDetailsService;
    private ArrayList<Vardenhet> enhets;
    private SakerhetstjanstAssertion assertion;

    @Before
    public void setUp() throws Exception {
        final UserDetailsService userDetailsService1 = new UserDetailsService();
        userDetailsService = Mockito.spy(userDetailsService1);
        assertion = Mockito.mock(SakerhetstjanstAssertion.class);
        Mockito.doReturn(assertion).when(userDetailsService).getSakerhetstjanstAssertion(Mockito.any(SAMLCredential.class));
        Mockito.doReturn(new Vardenhet()).when(userDetailsService).getLoginVerksamhet(Mockito.any(List.class), Mockito.anyString());
        HsaOrganizationsService hsaOrganizationsService = Mockito.mock(HsaOrganizationsService.class);
        ReflectionTestUtils.setField(userDetailsService, "hsaOrganizationsService", hsaOrganizationsService);
        enhets = new ArrayList<>();
        Mockito.when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(Mockito.anyString())).thenReturn(enhets);
    }

    @Test
    public void testGetLoginInfoWhenNotProcessledare() throws Exception {
        //Given
        Mockito.when(assertion.getSystemRoles()).thenReturn(vgAccessFlagNotSet());

        //When
        final User user = (User) userDetailsService.loadUserBySAML(null);

        //Then
        assertEquals(false, user.isProcessledare());
        assertEquals(false, user.isProcessledareDenied());
    }

    @Test
    public void testGetLoginInfoWhenProcessledareWithOneVg() throws Exception {
        //Given
        Mockito.when(assertion.getSystemRoles()).thenReturn(vgAccessFlagSet());
        enhets.add(createEnhet("vg1"));

        //When
        final User user = (User) userDetailsService.loadUserBySAML(null);

        //Then
        assertEquals(true, user.isProcessledare());
        assertEquals(false, user.isProcessledareDenied());
    }

    @Test
    public void testGetLoginInfoWhenProcessledareWithTwoVg() throws Exception {
        //Given
        Mockito.when(assertion.getSystemRoles()).thenReturn(vgAccessFlagSet());
        enhets.add(createEnhet("vg1"));
        enhets.add(createEnhet("vg2"));

        //When
        final User user = (User) userDetailsService.loadUserBySAML(null);

        //Then
        assertEquals(false, user.isProcessledare());
        assertEquals(true, user.isProcessledareDenied());
    }

    @Test
    public void testGetLoginInfoWhenProcessledareWithTwoEnhetsOnSameVg() throws Exception {
        //Given
        Mockito.when(assertion.getSystemRoles()).thenReturn(vgAccessFlagSet());
        enhets.add(createEnhet("vg1"));
        enhets.add(createEnhet("vg1"));

        //When
        final User user = (User) userDetailsService.loadUserBySAML(null);

        //Then
        assertEquals(true, user.isProcessledare());
        assertEquals(false, user.isProcessledareDenied());
    }

    private Vardenhet createEnhet(String vardgivareId) {
        return new Vardenhet("", "", vardgivareId);
    }

    private Collection<String> vgAccessFlagSet() {
        final ArrayList<String> flags = new ArrayList<>();
        flags.add(UserDetailsService.GLOBAL_VG_ACCESS_FLAG);
        return flags;
    }

    private Collection<String> vgAccessFlagNotSet() {
        return Collections.emptyList();
    }

}
