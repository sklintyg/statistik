/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General  License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General  License for more details.
 *
 * You should have received a copy of the GNU General  License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.auth.jwt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MissingClaimException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import se.inera.auth.model.User;
import se.inera.statistics.web.service.jwt.JwtValidationService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String USER_ID = "hsa-123";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtValidationService jwtValidationService;

    @InjectMocks
    private JwtAuthenticationFilter testee = new JwtAuthenticationFilter(new AntPathRequestMatcher("/"));

    private HttpServletRequest req = mock(HttpServletRequest.class);

    private HttpServletResponse resp = mock(HttpServletResponse.class);

    private Claims claims = mock(Claims.class);
    private Jws<Claims> jws = mock(Jws.class);

    @BeforeEach
    void init() {
        SecurityContextHolder.clearContext();
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("access_token")).thenReturn("access-token");
        when(authenticationManager.authenticate(any(JwtAuthenticationToken.class)))
            .thenReturn(new UsernamePasswordAuthenticationToken(mock(User.class), mock(User.class)));
    }

    @Test
    void testOkWithPlainEmployeeHsaIdParameter() {
        when(claims.get("employeeHsaId")).thenReturn(USER_ID);
        when(jws.getBody()).thenReturn(claims);
        when(jwtValidationService.validateJwsToken(anyString())).thenReturn(jws);
        testee.attemptAuthentication(req, resp);
    }

    @Test
    void testOkWithArrayListEmployeeHsaIdParameter() {
        when(claims.get("employeeHsaId")).thenReturn(Arrays.asList(USER_ID));
        when(jws.getBody()).thenReturn(claims);
        when(jwtValidationService.validateJwsToken(anyString())).thenReturn(jws);
        testee.attemptAuthentication(req, resp);
    }

    @Test
    void testOkWithBearerHeader() {
        when(req.getParameter("access_token")).thenReturn(null);
        when(req.getHeader("Authorization")).thenReturn("Bearer: access-token");
        when(claims.get("employeeHsaId")).thenReturn(USER_ID);
        when(jws.getBody()).thenReturn(claims);
        when(jwtValidationService.validateJwsToken(anyString())).thenReturn(jws);
        testee.attemptAuthentication(req, resp);
    }

    @Test
    void testReturnsUnsupportedEmployeeHsaIdClass() {
        when(claims.get("employeeHsaId")).thenReturn(123);
        when(jws.getBody()).thenReturn(claims);
        when(jwtValidationService.validateJwsToken(anyString())).thenReturn(jws);
        assertThrows(IncorrectClaimException.class, () -> testee.attemptAuthentication(req, resp));
    }

    @Test
    void testReturnsEmptyArrayListAsEmployeeHsaId() {
        when(claims.get("employeeHsaId")).thenReturn(new ArrayList<>());
        when(jws.getBody()).thenReturn(claims);
        when(jwtValidationService.validateJwsToken(anyString())).thenReturn(jws);
        assertThrows(IncorrectClaimException.class, () -> testee.attemptAuthentication(req, resp));
    }

    @Test
    void testReturnsNullEmployeeHsaIdClass() {
        when(claims.get("employeeHsaId")).thenReturn(null);
        when(jws.getBody()).thenReturn(claims);
        when(jwtValidationService.validateJwsToken(anyString())).thenReturn(jws);
        assertThrows(MissingClaimException.class, () -> testee.attemptAuthentication(req, resp));

    }

    @Test
    void testReturnsEmptyStringEmployeeHsaIdClass() {
        when(claims.get("employeeHsaId")).thenReturn("");
        when(jws.getBody()).thenReturn(claims);
        when(jwtValidationService.validateJwsToken(anyString())).thenReturn(jws);
        assertThrows(MissingClaimException.class, () -> testee.attemptAuthentication(req, resp));
    }

    @Test
    void testPostContainsNoTokenInHeaderOrBody() {
        when(req.getParameter("access_token")).thenReturn(null);
        assertThrows(AuthenticationServiceException.class, () -> testee.attemptAuthentication(req, resp));
    }

    @Test
    void testThrowsExceptionIfUnsupportedMethod() {
        when(req.getMethod()).thenReturn("GET");
        assertThrows(AuthenticationServiceException.class, () -> testee.attemptAuthentication(req, resp));
    }

    @Test
    void testThrowsExceptionIfNotParameterNorBearerHeader() {
        when(req.getParameter("access_token")).thenReturn(null);
        when(req.getHeader("Authorization")).thenReturn("Basic");
        assertThrows(AuthenticationServiceException.class, () -> testee.attemptAuthentication(req, resp));
    }

}