/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.config;


import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import se.inera.intyg.infra.security.common.cookie.IneraCookieSerializer;

//import org.apache.cxf.feature.LoggingFeature;

@Configuration
@EnableTransactionManagement
@DependsOn("transactionManager")
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource(ignoreResourceNotFound = true, value = "classpath:version.properties"),
    @PropertySource(ignoreResourceNotFound = true, value = "file:${dev.config.file}")
})
public class ApplicationConfig implements TransactionManagementConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    private Bus bus;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public CookieSerializer cookieSerializer() {
        /*
        This is needed to make IdP functionality work.
        This will not satisfy all browsers, but it works for IE, Chrome and Edge.
        Reference: https://auth0.com/blog/browser-behavior-changes-what-developers-need-to-know/
         */
        return new IneraCookieSerializer();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @PostConstruct
    public Bus init() {

        logSystemInfo();

        bus.setFeatures(new ArrayList<>(Arrays.asList(loggingFeature())));
        return bus;
    }

    private void logSystemInfo() {
        Locale locale = Locale.getDefault();
        LOG.info("DefaultTimeZone: " + TimeZone.getDefault().getDisplayName() + ", DefaultZoneId: " + ZoneId.systemDefault().getId());
        LOG.info(
            "defaultLang: " + locale.getDisplayLanguage() + ", defaultCountry: " + locale.getDisplayCountry() + ", defaultVariant: "
                + locale
                .getDisplayVariant() + ", defaultScript: " + locale.getDisplayScript() + ", defaultName: " + locale.getDisplayName());
        LOG.info("user.language: " + System.getProperty("user.language") + ", user.country: " + System.getProperty("user.country")
            + ", user.variant: " + System.getProperty("user.variant") + "user.script: " + System.getProperty("user.script"));
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("version");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public LoggingFeature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }
}
