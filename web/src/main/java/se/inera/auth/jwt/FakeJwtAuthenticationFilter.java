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

import com.google.common.base.Strings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Custom authentication filter that supports extraction of JWT tokens from either an Authorization: Bearer: token
 * HTTP header or from x-www-form-urlencoded POST form data.
 */
public class FakeJwtAuthenticationFilter extends JwtAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(FakeJwtAuthenticationFilter.class);

    private static final String FAKE_TOKEN_PREFIX = "fakeToken-";

    protected FakeJwtAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
        LOG.error("---- FakeAuthentication enabled. DO NOT USE IN PRODUCTION!!! ----");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String jwsToken = extractAccessToken(request);

        if (jwsToken.startsWith(FAKE_TOKEN_PREFIX)) {
            return fakeAuthenticate(jwsToken);
        }

        return authenticate(jwsToken);
    }

    private Authentication fakeAuthenticate(String jwsToken) {
        String employeeHsaId = jwsToken.substring(FAKE_TOKEN_PREFIX.length());

        if (Strings.isNullOrEmpty(employeeHsaId)) {
            throw new AuthenticationServiceException("Could not extract hsaId from token");
        }

        // Build authentication token and proceed with authorization.
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(employeeHsaId));
    }
}
