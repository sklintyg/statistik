package se.inera.testability;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import se.inera.auth.FakeAuthenticationToken;
import se.inera.auth.FakeCredentials;
import se.inera.auth.LoginMethod;
import se.inera.auth.UserDetailsService;

@Service
@RequiredArgsConstructor
public class FakeLoginService {

    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void login(FakeCredentials fakeCredentials, HttpServletRequest request) {
        final var oldSession = request.getSession(false);
        Optional.ofNullable(oldSession).ifPresent(HttpSession::invalidate);

        final var fakeAuthenticationToken = new FakeAuthenticationToken(
            userDetailsService.buildUserPrincipal(
                fakeCredentials.getHsaId(), LoginMethod.FAKE
            )
        );

        final var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(fakeAuthenticationToken);
        SecurityContextHolder.setContext(context);

        final var newSession = request.getSession(true);
        newSession.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context
        );

        applicationEventPublisher.publishEvent(
            new InteractiveAuthenticationSuccessEvent(
                fakeAuthenticationToken, this.getClass()
            )
        );
    }

    public void logout(HttpSession session) {
        if (session == null) {
            return;
        }

        session.invalidate();

        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();

        if (authentication != null) {
            applicationEventPublisher.publishEvent(new LogoutSuccessEvent(authentication));
        }
    }
}