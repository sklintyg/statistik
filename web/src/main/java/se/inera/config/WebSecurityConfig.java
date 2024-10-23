package se.inera.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import se.inera.auth.CsrfCookieFilter;
import se.inera.auth.StatistikCookieSerializer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public static final String TESTABILITY_PROFILE = "testability";

    @Value("${saml.logout.success.url:/}")
    private String samlLogoutSuccessUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(request -> request.
                requestMatchers("/api/**").permitAll().
                requestMatchers("/api/login/**").permitAll().
                requestMatchers("/api/links/**").permitAll().
                requestMatchers("/api/logging/**").permitAll().
                requestMatchers("/api/verksamhet/**").fullyAuthenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .cors(Customizer.withDefaults())
            .logout(logout -> logout.logoutSuccessUrl(samlLogoutSuccessUrl));

        return http.build();
    }

    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        return new StatistikCookieSerializer(true);
    }
}