/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.logging;

import java.io.InputStream;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.google.common.base.Strings;

public class LogbackConfiguratorContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackConfiguratorContextListener.class);

    private static final String CLASSPATH = "classpath:";
    private static final String DEFAULTURI = CLASSPATH + "logback.xml";

    /**
     * initialize logback with external configuration file.
     */
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        final Resource resource = getConfigurationResource(getConfigurationUri(servletContextEvent));

        if (!resource.exists()) {
            LOGGER.error("Can't read logback configuration from "
                    + resource.getDescription() + " - Keep default configuration");
            return;
        }

        LOGGER.info("Found logback configuration " + resource.getDescription()
                + " - Overriding default configuration");
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();
            final InputStream in = resource.getInputStream();
            configurator.doConfigure(in);
            in.close();
            StatusPrinter.printIfErrorsOccured(loggerContext);
            loggerContext.start();
        } catch (Exception ex) {
            try {
                new ContextInitializer(loggerContext).autoConfig();
            } catch (JoranException e) {
                LOGGER.error("Can't fallback to default (auto) configuration", e);
            }
            LOGGER.error(
                    "Can't configure logback from " + resource.getDescription()
                            + " - Keep default configuration", ex);
        }
    }

    private Resource getConfigurationResource(final String uri) {
        return uri.startsWith(CLASSPATH)
                ? new ClassPathResource(uri.substring(CLASSPATH.length()))
                : new FileSystemResource(uri);
    }

    private String getConfigurationUri(final ServletContextEvent ctx) {
        final String name = ctx.getServletContext().getInitParameter("logbackConfigParameter");
        return Strings.isNullOrEmpty(name) ? DEFAULTURI : System.getProperty(name, DEFAULTURI);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Do nothing
    }
}
