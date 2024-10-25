package se.inera.auth;

import java.util.Objects;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

public class Saml2AuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    @Getter
    private final Saml2Authentication saml2Authentication;
    private final String name;

    public Saml2AuthenticationToken(Object principal, Saml2Authentication authentication) {
        super(authentication.getAuthorities());
        this.principal = principal;
        this.saml2Authentication = authentication;
        this.name = authentication.getName();
    }

    @Override
    public Object getCredentials() {
        return saml2Authentication.getCredentials();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return name;
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
        Saml2AuthenticationToken token = (Saml2AuthenticationToken) o;
        return Objects.equals(saml2Authentication, token.saml2Authentication) && Objects.equals(name, token.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), principal, saml2Authentication, name);
    }
}