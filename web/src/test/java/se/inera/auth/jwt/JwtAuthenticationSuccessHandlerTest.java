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
package se.inera.auth.jwt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import se.inera.auth.model.User;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.integration.hsa.model.Vardenhet;
import se.inera.statistics.web.service.FilterHashHandler;
import se.inera.statistics.web.service.exception.FilterException;


@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationSuccessHandlerTest {

    @Mock
    private RedirectStrategy redirectStrategy;

    @Mock
    private FilterHashHandler filterHashHandler;

    @InjectMocks
    private JwtAuthenticationSuccessHandler testee;

    @BeforeEach
    public void setup() {
        testee.setRedirectStrategy(redirectStrategy);
    }

    @Test
    public void onAuthenticationSuccess() throws IOException, FilterException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = Mockito.mock(User.class);

        List<Vardenhet> vardenhetsList = new ArrayList<>();
        vardenhetsList.add(new Vardenhet(new HsaIdEnhet("hsaId"), "enhet", new HsaIdVardgivare("vgid")));

        when(filterHashHandler.getHash(anyString())).thenReturn("filterhash");
        when(request.getParameter("enhet")).thenReturn("hsaId");
        when(request.getRequestURI()).thenReturn("/oauth/token/view");
        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getVardenhetList()).thenReturn(vardenhetsList);

        testee.onAuthenticationSuccess(request, response, authentication);

        verify(redirectStrategy).sendRedirect(request, response, "/#/sso?vgid=VGID&filter=filterhash");
    }

    @Test
    public void onAuthenticationSuccessNoRequestedEnhet() throws IOException, FilterException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = Mockito.mock(User.class);

        List<Vardenhet> vardenhetsList = new ArrayList<>();
        vardenhetsList.add(new Vardenhet(new HsaIdEnhet("otherHsaId"), "enhet", new HsaIdVardgivare("vgid")));

        when(request.getParameter("enhet")).thenReturn("hsaId");
        when(request.getRequestURI()).thenReturn("/oauth/token/view");
        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getVardenhetList()).thenReturn(vardenhetsList);

        testee.onAuthenticationSuccess(request, response, authentication);

        verify(redirectStrategy).sendRedirect(request, response, "/");
    }

    @Test
    public void onAuthenticationSuccessNoMedarbetaruppdrag() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = Mockito.mock(User.class);

        when(request.getRequestURI()).thenReturn("/oauth/token/view");
        when(authentication.getPrincipal()).thenReturn(user);

        testee.onAuthenticationSuccess(request, response, authentication);

        verify(redirectStrategy).sendRedirect(request, response, "/logout");
    }

    @Test
    public void onAuthenticationSuccessWrongUrl() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = Mockito.mock(User.class);

        when(request.getRequestURI()).thenReturn("/notfound");
        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> testee.onAuthenticationSuccess(request, response, authentication));
    }

    @Test
    public void onAuthenticationSuccessTestNoUser() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> testee.onAuthenticationSuccess(request, response, authentication));
    }
}