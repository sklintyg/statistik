package se.inera.auth;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import se.inera.auth.model.User;
import se.inera.auth.model.VerksamhetMapperObject;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.web.model.Verksamhet;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author andreaskaltenbach
 */
public class FakeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(FakeAuthenticationFilter.class);

    @Value("${spring.profiles.active}")
    private String profiles;

    protected FakeAuthenticationFilter() {
        super("/fake");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Hantera mer än en profil
//        if ( !"dev".equals(profiles) && !"test".equals(profiles) && !"qa".equals(profiles)) {
          if (!profiles.contains("dev") && !profiles.contains("test") && !profiles.contains("qa")) {
            return null;
        }

        String parameter = request.getParameter("userJsonDisplay");
        // we manually encode the json parameter
        String json = URLDecoder.decode(parameter, "ISO-8859-1");
        if (json == null) {
            return null;
        }

        try {
            FakeCredentials fakeCredentials = new ObjectMapper().readValue(json, FakeCredentials.class);
            LOG.info("Detected fake credentials " + fakeCredentials);
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("principal", "credentials", new ArrayList<GrantedAuthority>());
            final List<Vardenhet> vardenhets = new ArrayList<>();
            for (VerksamhetMapperObject verksamhet : fakeCredentials.getVardenhets()) {
                vardenhets.add(new Vardenhet(verksamhet.getId(), verksamhet.getName()));
            }
            token.setDetails(new User(fakeCredentials.getHsaId(), fakeCredentials.getFornamn() + " " + fakeCredentials.getEfternamn(), vardenhets));
            return token;
        } catch (IOException e) {
            String message = "Failed to parse JSON: " + json;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
