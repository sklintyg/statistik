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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * @author andreaskaltenbach
 */
public class FakeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(FakeAuthenticationFilter.class);

    protected FakeAuthenticationFilter() {
        super("/fake");
        LOG.error("---- FakeAuthentication enabled. DO NOT USE IN PRODUCTION!!! ----");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String parameter = request.getParameter("userJsonDisplay");
        // we manually encode the json parameter
        String json = URLDecoder.decode(parameter, StandardCharsets.ISO_8859_1);

        try {
            FakeCredentials fakeCredentials = new ObjectMapper().readValue(json, FakeCredentials.class);
            LOG.info("Detected fake credentials " + fakeCredentials);
            return getAuthenticationManager().authenticate(new FakeAuthenticationToken(fakeCredentials));
        } catch (IOException e) {
            String message = "Failed to parse JSON: " + json;
            LOG.error(message, e);
            throw new FakeAuthJsonParseFailedException(message, e);
        }
    }
}
