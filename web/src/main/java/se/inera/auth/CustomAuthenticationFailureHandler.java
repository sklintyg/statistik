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
public class CustomAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    private final MonitoringLogService monitoringLogService;
    private static final String DEFAULT_FAILURE_URL = "/#/login?error=loginfailed";

    private static final Map<String, String> failureUrls = Map.of(
        BadCredentialsException.class.getName(), DEFAULT_FAILURE_URL,
        MissingMedarbetaruppdragException.class.getName(), "/#/login?error=medarbetaruppdrag"
    );

    public CustomAuthenticationFailureHandler(MonitoringLogService monitoringLogService) {
        this.monitoringLogService = monitoringLogService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {
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

    private void handleLogEvents(AuthenticationException exception,
        String errorId) {
        log.error(
            String.format("Failed login attempt with errorId '%s' - exception %s", errorId, exception)
        );
        monitoringLogService.logUserLoginFailed(exception.getMessage());
    }
}