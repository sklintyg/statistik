package se.inera.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static se.inera.auth.AuthenticationConstants.EMPLOYEE_HSA_ID;
import static se.inera.auth.AuthenticationConstants.RELYING_PARTY_REGISTRATION_ID;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import lombok.RequiredArgsConstructor;
import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.OpenSaml4LogoutRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import se.inera.auth.CsrfCookieFilter;
import se.inera.auth.CustomAuthenticationFailureHandler;
import se.inera.auth.LoginMethod;
import se.inera.auth.Saml2AuthenticationToken;
import se.inera.auth.SpaCsrfTokenRequestHandler;
import se.inera.auth.UserDetailsService;
import se.inera.intyg.infra.security.common.cookie.IneraCookieSerializer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ComponentScan({"se.inera.auth", "se.inera.statistics.web.service.monitoring"})
public class WebSecurityConfig {

    private static final String REGION_FILEUPLOAD = "/api/region/fileupload";
    private final Environment environment;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    public static final String TESTABILITY_PROFILE = "testability";
    public static final String TESTABILITY_API = "/api/testability/**";
    @Value("${saml.sp.entity.id}")
    private String samlEntityId;
    @Value("${saml.idp.metadata.file}")
    private String samlIdpMetadataLocation;
    @Value("${saml.sp.assertion.consumer.service.location}")
    private String assertionConsumerServiceLocation;
    @Value("${saml.sp.single.logout.service.location}")
    private String singleLogoutServiceLocation;
    @Value("${saml.sp.single.logout.service.response.location}")
    private String singleLogoutServiceResponseLocation;
    @Value("${saml.login.success.url}")
    private String samlLoginSuccessUrl;
    @Value("${saml.login.success.url.always.use}")
    private boolean samlLoginSuccessUrlAlwaysUse;
    @Value("${saml.logout.success.url}")
    private String samlLogoutSuccessUrl;
    @Value("${saml.keystore.type:PKCS12}")
    private String keyStoreType;
    @Value("${saml.keystore.file}")
    private String keyStorePath;
    @Value("${saml.keystore.alias}")
    private String keyAlias;
    @Value("${saml.keystore.password}")
    private String keyStorePassword;

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository()
        throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {

        final var keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(new FileInputStream(ResourceUtils.getFile(keyStorePath)), keyStorePassword.toCharArray());
        final var appPrivateKey = (PrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
        final var appCertificate = (X509Certificate) keyStore.getCertificate(keyAlias);

        final var registration = RelyingPartyRegistrations
            .fromMetadataLocation(samlIdpMetadataLocation)
            .registrationId(RELYING_PARTY_REGISTRATION_ID)
            .entityId(samlEntityId)
            .assertionConsumerServiceLocation(assertionConsumerServiceLocation)
            .singleLogoutServiceLocation(singleLogoutServiceLocation)
            .singleLogoutServiceResponseLocation(singleLogoutServiceResponseLocation)
            .signingX509Credentials(signing ->
                signing.add(
                    Saml2X509Credential.signing(appPrivateKey, appCertificate)
                )
            )
            .build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RelyingPartyRegistrationRepository relyingPartyRegistrationRepository,
        Saml2LogoutRequestResolver logoutRequestResolver)
        throws Exception {

        if (environment.acceptsProfiles(Profiles.of(TESTABILITY_PROFILE))) {
            configureTestability(http);
        }

        http
            .authorizeHttpRequests(request -> request
                .requestMatchers("/*").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/app/**").permitAll()
                .requestMatchers("/components/**").permitAll()
                .requestMatchers("/bower_components/**").permitAll()
                .requestMatchers("/assets/**").permitAll()
                .requestMatchers("/api/links/**").permitAll()
                .requestMatchers("/api/internalapi/**").permitAll()
                .requestMatchers("/services/api/ia-api/**").permitAll()
                .requestMatchers("/api/logging/monitorlog").permitAll()
                .requestMatchers("/api/login/getAppSettings").permitAll()
                .anyRequest().fullyAuthenticated()
            )
            .saml2Metadata(withDefaults())
            .saml2Login(saml2 -> saml2
                .relyingPartyRegistrationRepository(relyingPartyRegistrationRepository)
                .authenticationManager(
                    new ProviderManager(
                        getOpenSaml4AuthenticationProvider()
                    )
                )
                .failureHandler(customAuthenticationFailureHandler)
                .defaultSuccessUrl(samlLoginSuccessUrl, samlLoginSuccessUrlAlwaysUse)
            )
            .saml2Logout(saml2 -> saml2.logoutRequest(logout -> logout.logoutRequestResolver(logoutRequestResolver)))
            .logout(logout ->
                logout.logoutSuccessUrl(samlLogoutSuccessUrl)
            )
            .requestCache(cacheConfigurer -> cacheConfigurer
                .requestCache(
                    samlLoginSuccessUrlAlwaysUse
                        ? new NullRequestCache()
                        : new HttpSessionRequestCache()
                )
            )
            .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            )
            .csrf(
                csrfConfigurer -> csrfConfigurer
                    .ignoringRequestMatchers(
                        antMatcher(REGION_FILEUPLOAD),
                        antMatcher("/api/internalapi/**")
                    )
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    private void configureTestability(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(request -> request
                .requestMatchers(TESTABILITY_API).permitAll()
            )
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/testsupport/**").permitAll()
            )
            .csrf(csrfConfigurer -> csrfConfigurer
                .ignoringRequestMatchers(TESTABILITY_API, "/api/testsupport/**")
            );
    }

    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        return new IneraCookieSerializer();
    }

    @Bean
    public Saml2LogoutRequestResolver logoutRequestResolver(RelyingPartyRegistrationRepository registrations) {
        final var logoutRequestResolver = new OpenSaml4LogoutRequestResolver(registrations);
        logoutRequestResolver.setParametersConsumer(parameters -> {
            final var token = (Saml2AuthenticationToken) parameters.getAuthentication();
            final var principal = (DefaultSaml2AuthenticatedPrincipal) token.getSaml2Authentication().getPrincipal();
            final var name = principal.getName();
            final var format = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
            parameters.getLogoutRequest();
            final var logoutRequest = parameters.getLogoutRequest();
            final var nameId = logoutRequest.getNameID();
            nameId.setValue(name);
            nameId.setFormat(format);

            final var sessionIndex = new MySessionIndex("urn:oasis:names:tc:SAML:2.0:protocol", "SessionIndex", "saml2p");
            sessionIndex.setValue(principal.getSessionIndexes().getFirst());
            logoutRequest.getSessionIndexes().add(sessionIndex);
        });
        return logoutRequestResolver;
    }

    public class MySessionIndex extends XSStringImpl implements SessionIndex {

        public MySessionIndex(String namespaceURI, String elementLocalName, String namespacePrefix) {
            super(namespaceURI, elementLocalName, namespacePrefix);
        }
    }

    private OpenSaml4AuthenticationProvider getOpenSaml4AuthenticationProvider() {
        final var authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(responseToken -> {
            final var authentication = OpenSaml4AuthenticationProvider
                .createDefaultResponseAuthenticationConverter()
                .convert(responseToken);
            if (!(authentication != null && authentication.isAuthenticated())) {
                return null;
            }
            final var personId = getAttribute(authentication);
            final var principal = userDetailsService.buildUserPrincipal(personId, LoginMethod.SITHS);
            final var saml2AuthenticationToken = new Saml2AuthenticationToken(principal, authentication);
            saml2AuthenticationToken.setAuthenticated(true);
            return saml2AuthenticationToken;
        });
        return authenticationProvider;
    }

    private String getAttribute(Saml2Authentication samlCredential) {
        final var principal = (DefaultSaml2AuthenticatedPrincipal) samlCredential.getPrincipal();
        final var attributes = principal.getAttributes();
        if (attributes.containsKey(EMPLOYEE_HSA_ID)) {
            return (String) attributes.get(EMPLOYEE_HSA_ID).getFirst();
        }
        throw new IllegalArgumentException(
            "Could not extract attribute '" + EMPLOYEE_HSA_ID + "' from Saml2Authentication.");
    }

    @Bean
    public Saml2AuthenticationRequestResolver authenticationRequestResolver(RelyingPartyRegistrationRepository registrations) {
        RelyingPartyRegistrationResolver registrationResolver =
            new DefaultRelyingPartyRegistrationResolver(registrations);
        OpenSaml4AuthenticationRequestResolver authenticationRequestResolver =
            new OpenSaml4AuthenticationRequestResolver(registrationResolver);
        authenticationRequestResolver.setAuthnRequestCustomizer((context) -> {
                context.getAuthnRequest().setAttributeConsumingServiceIndex(1);
                context.getAuthnRequest().setRequestedAuthnContext(buildRequestedAuthnContext());
            }
        );
        return authenticationRequestResolver;
    }

    private RequestedAuthnContext buildRequestedAuthnContext() {
        final var authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
        final var authnContextClassRefLoa2 = authnContextClassRefBuilder.buildObject(SAMLConstants.SAML20_NS,
            AuthnContextClassRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final var authnContextClassRefLoa3 = authnContextClassRefBuilder.buildObject(SAMLConstants.SAML20_NS,
            AuthnContextClassRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        authnContextClassRefLoa2.setURI("http://id.sambi.se/loa/loa2");
        authnContextClassRefLoa3.setURI("http://id.sambi.se/loa/loa3");

        final var requestedAuthnContextBuilder = new RequestedAuthnContextBuilder();
        final var requestedAuthnContext = requestedAuthnContextBuilder.buildObject();
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRefLoa2);
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRefLoa3);

        return requestedAuthnContext;
    }
}