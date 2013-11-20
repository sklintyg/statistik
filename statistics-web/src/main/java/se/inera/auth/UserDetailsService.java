package se.inera.auth;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.HsaOrganizationsService;

public class UserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        LOG.info("User authentication was successful. SAML credential is " + credential);

        SakerhetstjanstAssertion assertion = new SakerhetstjanstAssertion(credential.getAuthenticationAssertion());

        List<Vardenhet> authorizedVerksamhets = hsaOrganizationsService.getAuthorizedEnheterForHosPerson(assertion.getHsaId());
        User user = new User(assertion.getHsaId(), assertion.getFornamn() + ' ' + assertion.getMellanOchEfternamn(), authorizedVerksamhets);

        return user;

    }

}
