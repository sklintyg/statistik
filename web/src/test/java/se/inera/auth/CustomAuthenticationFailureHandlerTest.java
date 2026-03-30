/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFailureHandlerTest {

  @Mock MonitoringLogService monitoringLogService;
  @InjectMocks CustomAuthenticationFailureHandler handler;

  @Test
  void shallMonitorLogUserLoginFailed() throws IOException {
    final var expectedErrorMessage = "expectedErrorMessage";
    final var request = mock(HttpServletRequest.class);
    final var response = mock(HttpServletResponse.class);
    final var exception = mock(AuthenticationException.class);
    final var httpSession = mock(HttpSession.class);

    doReturn(expectedErrorMessage).when(exception).getMessage();
    doReturn(httpSession).when(request).getSession(false);
    doReturn(httpSession).when(request).getSession();

    handler.onAuthenticationFailure(request, response, exception);
    verify(monitoringLogService).logUserLoginFailed(expectedErrorMessage);
  }
}
