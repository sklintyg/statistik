package se.inera.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Value("${error.login.url}")
    private String errorLoginUrl;
    private final MonitoringLogService monitoringLogService;

    public CustomAuthenticationFailureHandler(MonitoringLogService monitoringLogService) {
        this.monitoringLogService = monitoringLogService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {
        final var errorId = String.valueOf(UUID.randomUUID());
        handleLogEvents(exception, errorId);
        response.sendRedirect(request.getContextPath() + errorLoginUrl);
    }

    private void handleLogEvents(AuthenticationException exception,
        String errorId) {
        log.error(
            String.format("Failed login attempt with errorId '%s' - exception %s", errorId, exception)
        );
        monitoringLogService.logUserLoginFailed(exception.getMessage());
    }
}