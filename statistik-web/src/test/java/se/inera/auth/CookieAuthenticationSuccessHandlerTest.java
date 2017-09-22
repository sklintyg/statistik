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
package se.inera.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CookieAuthenticationSuccessHandlerTest {

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private CookieAuthenticationSuccessHandler cas = new CookieAuthenticationSuccessHandler();

    @Test
    public void testOnAuthenticationSuccessRedirectDefault() throws IOException, ServletException {
        final String defaultTargetUrl = "url";

        cas.setDefaultTargetUrl(defaultTargetUrl);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[0]);

        cas.onAuthenticationSuccess(request, null, null);

        verify(redirectStrategy).sendRedirect(any(), any(), eq(defaultTargetUrl));
    }

    @Test
    public void testOnAuthenticationSuccessRedirectCookie() throws IOException, ServletException {
        final String targetUrl = "targetUrl";

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = {new Cookie("statUrl", targetUrl)};
        when(request.getCookies()).thenReturn(cookies);

        cas.onAuthenticationSuccess(request, null, null);

        verify(redirectStrategy).sendRedirect(any(), any(), eq(targetUrl));
    }
}
