/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Successhandler which redirects to the url specified in a cookie or to a default URL (specified when creating the
 * bean).
 */
public class CookieAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String COOKIE_NAME = "statUrl";

    private String defaultTargetUrl;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {

        if (request == null || request.getCookies() == null) {
            redirectStrategy.sendRedirect(request, response, defaultTargetUrl);
            return;
        }

        Optional<Cookie> cookie = Stream.of(request.getCookies())
            .filter(c -> COOKIE_NAME.equals(c.getName()))
            .findAny();
        if (cookie.isPresent()) {
            redirectStrategy.sendRedirect(request, response, URLDecoder.decode(cookie.get().getValue(), StandardCharsets.UTF_8.name()));
        } else {
            redirectStrategy.sendRedirect(request, response, defaultTargetUrl);
        }
    }

    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }
}
