package se.inera.auth;

import java.io.Serial;
import java.util.Collections;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import se.inera.auth.model.User;

public class FakeAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    private final User user;

    public FakeAuthenticationToken(User user) {
        super(Collections.emptyList());
        this.user = user;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return user.getHsaId();
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final var that = (FakeAuthenticationToken) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }
}