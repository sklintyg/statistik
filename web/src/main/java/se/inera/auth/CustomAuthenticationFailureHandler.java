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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import se.inera.auth.exceptions.MissingMedarbetaruppdragException;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler
    extends ExceptionMappingAuthenticationFailureHandler {

  private final MonitoringLogService monitoringLogService;
  private static final String DEFAULT_FAILURE_URL = "/#/login?error=loginfailed";

  private static final Map<String, String> failureUrls =
      Map.of(
          BadCredentialsException.class.getName(),
          DEFAULT_FAILURE_URL,
          MissingMedarbetaruppdragException.class.getName(),
          "/#/login?error=medarbetaruppdrag");

  public CustomAuthenticationFailureHandler(MonitoringLogService monitoringLogService) {
    this.monitoringLogService = monitoringLogService;
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    final var exceptionName = exception.getClass().getName();
    final var errorId = String.valueOf(UUID.randomUUID());
    handleLogEvents(exception, errorId);
    String url;
    if (failureUrls.containsKey(exceptionName)) {
      url = failureUrls.get(exceptionName);
    } else {
      saveException(request, exception);
      url = DEFAULT_FAILURE_URL;
    }
    getRedirectStrategy().sendRedirect(request, response, url);
  }

  private void handleLogEvents(AuthenticationException exception, String errorId) {
    log.error(
        String.format("Failed login attempt with errorId '%s' - exception %s", errorId, exception));
    monitoringLogService.logUserLoginFailed(exception.getMessage());
  }
}
