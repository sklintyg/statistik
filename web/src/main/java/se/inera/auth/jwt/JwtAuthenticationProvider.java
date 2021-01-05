/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import se.inera.auth.UserDetailsService;
import se.inera.auth.model.User;

/**
 * JWT based authentication provider. Given that the supplied {@link Authentication}
 * is supported, the standard Webcert UserDetailsService is used to check user authorization and build the user
 * principal.
 *
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new AuthenticationServiceException("Unsupported Authentication. Expected JwtAuthenticationToken, got "
                + authentication.getClass().getName());
        }

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        User user = userDetailsService.loadUserByHsaId(jwtAuthenticationToken.getUserHsaId());
        if (user != null) {

            ExpiringUsernameAuthenticationToken result = new ExpiringUsernameAuthenticationToken(null, user, jwtAuthenticationToken,
                new ArrayList<>());
            result.setDetails(user);
            return result;
        }
        throw new AuthenticationServiceException("User principal returned from UserDetailsService was not of type User,"
            + " throwing exception.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
