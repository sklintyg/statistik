package se.inera.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import se.inera.auth.model.User;

public class WebCertUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(WebCertUserDetailsService.class);

//    private static final String LAKARE = "Läkare";
//    private static final String LAKARE_CODE = "204010";

//    @Autowired
//    private HsaOrganizationsService hsaOrganizationsService;

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        LOG.info("User authentication was successful. SAML credential is " + credential);

//        SakerhetstjanstAssertion assertion = new SakerhetstjanstAssertion(credential.getAuthenticationAssertion());
//
//        WebCertUser webCertUser = createWebCertUser(assertion);
//
//        List<Vardgivare> authorizedVardgivare = hsaOrganizationsService.getAuthorizedEnheterForHosPerson(webCertUser.getHsaId());
//
//        // if user does not have access to any vardgivare, we have to reject authentication
//        if (authorizedVardgivare.isEmpty()) {
//            throw new MissingMedarbetaruppdragException(webCertUser.getHsaId());
//        }
//
//        webCertUser.setVardgivare(authorizedVardgivare);
//        return webCertUser;

        User user = new User();
        user.firstName = "FirstTest";
        user.lastName = "LastTest";
        user.hsaId = "HsaIdTest";
        return user;
    }

//    private WebCertUser createWebCertUser(SakerhetstjanstAssertion assertion) {
//        WebCertUser webcertUser = new WebCertUser();
//        webcertUser.setHsaId(assertion.getHsaId());
//        webcertUser.setNamn(assertion.getFornamn() + " " + assertion.getMellanOchEfternamn());
//        webcertUser.setForskrivarkod(assertion.getForskrivarkod());
//
//        // lakare flag is calculated by checking for lakare profession in title and title code
//        webcertUser.setLakare(LAKARE.equals(assertion.getTitel()) || LAKARE_CODE.equals(assertion.getTitelKod()));
//
//        return webcertUser;
//    }
}
