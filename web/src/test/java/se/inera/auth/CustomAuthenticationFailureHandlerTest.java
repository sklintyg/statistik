package se.inera.auth;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFailureHandlerTest {

    private static final String ERROR_URL = "errorUrl";
    @Mock
    MonitoringLogService monitoringLogService;
    @InjectMocks
    CustomAuthenticationFailureHandler handler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "errorLoginUrl", ERROR_URL);
    }

    @Test
    void shallMonitorLogUserLoginFailed() throws IOException {
        final var expectedErrorMessage = "expectedErrorMessage";
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var exception = mock(AuthenticationException.class);

        doReturn(expectedErrorMessage).when(exception).getMessage();

        handler.onAuthenticationFailure(request, response, exception);
        verify(monitoringLogService).logUserLoginFailed(expectedErrorMessage);
    }

    @Test
    void shallRedirectToErrorUrl() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var exception = mock(AuthenticationException.class);

        doReturn("").when(request).getContextPath();

        handler.onAuthenticationFailure(request, response, exception);
        verify(response).sendRedirect(ERROR_URL);
    }
}