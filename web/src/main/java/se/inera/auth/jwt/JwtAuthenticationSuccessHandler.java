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

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import se.inera.auth.model.User;
import se.inera.statistics.integration.hsa.model.Vardenhet;
import se.inera.statistics.web.service.exception.FilterException;
import se.inera.statistics.web.service.FilterHashHandler;

/**
 * Custom Spring Security {@link AuthenticationSuccessHandler} that post-authorization can augment the created
 * session redirect the user to the originally requested resource given a enhet parameter.
 */
public class JwtAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationSuccessHandler.class);

    @Autowired
    private FilterHashHandler filterHashHandler;

    public JwtAuthenticationSuccessHandler() {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException {

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect");
            return;
        }

        clearAuthenticationAttributes(request);

        String requestURI = request.getRequestURI();

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            // Should never happen...
            throw new IllegalStateException("No user principal, cannot bind integration params.");
        }

        String enhet = request.getParameter("enhet");

        String redirectUrl = "/";

        if (requestURI.endsWith("view")) {
            // Try to find requested unit
            Optional<Vardenhet> vardenhetOptional = user.getVardenhetList().stream()
                .filter(v -> v.getId().getId().equalsIgnoreCase(enhet))
                .findAny();

            if (vardenhetOptional.isPresent()) {
                Vardenhet vardenhet = vardenhetOptional.get();
                String vgId = vardenhet.getVardgivarId().getId();
                String enhetId = vardenhet.getId().getId();

                // Create filter with requested unit
                String filterData = "{\"enheter\": [\"" + enhetId + "\"]}";
                String filterHash = "";
                try {
                    filterHash = filterHashHandler.getHash(filterData);
                } catch (FilterException e) {
                    LOG.info("Not possible to create filter", e);
                }

                // Select caretaker and use filter with unit
                redirectUrl += "#/sso?vgid=" + vgId + "&filter=" + filterHash;
            } else {
                LOG.info("User don't have access to requested unit.");

                // Logout user if is doesn't have access to any unit in statistik
                if (user.getVardenhetList().isEmpty()) {
                    redirectUrl = "/logout";
                }
            }

        } else {
            throw new RuntimeException("The context path for JWT authentication was invalid {" + requestURI + "}");
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}