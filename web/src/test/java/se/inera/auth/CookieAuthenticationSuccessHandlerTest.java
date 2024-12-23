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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;

@ExtendWith(MockitoExtension.class)
public class CookieAuthenticationSuccessHandlerTest {

    @Mock
    private RedirectStrategy redirectStrategy;

    private Authentication authentication;
    private HttpServletResponse resp = mock(HttpServletResponse.class);

    @InjectMocks
    private CookieAuthenticationSuccessHandler cas = new CookieAuthenticationSuccessHandler();

    @BeforeEach
    public void init() {
        this.authentication = mock(Authentication.class);
    }

    @Test
    public void testOnAuthenticationSuccessRedirectDefault() throws IOException, ServletException {
        final String defaultTargetUrl = "url";

        cas.setDefaultTargetUrl(defaultTargetUrl);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(new Cookie[0]);

        cas.onAuthenticationSuccess(request, resp, authentication);

        verify(redirectStrategy).sendRedirect(any(), any(), eq(defaultTargetUrl));
    }

    @Test
    public void testOnAuthenticationSuccessRedirectCookie() throws IOException, ServletException {
        final String targetUrl = "targetUrl";

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = {new Cookie("statUrl", targetUrl)};
        when(request.getCookies()).thenReturn(cookies);

        cas.onAuthenticationSuccess(request, resp, authentication);

        verify(redirectStrategy).sendRedirect(any(), any(), eq(targetUrl));
    }
}