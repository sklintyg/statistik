/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.logging;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LogbackConfiguratorContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackConfiguratorContextListener.class);

    private static final String CLASSPATH = "classpath:";

    /**
     * initialize logback with external configuration file.
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String logbackConfigurationUri = getConfigurationUri(servletContextEvent);
        Resource logbackConfigurationResource = getConfigurationResource(logbackConfigurationUri);
        if (logbackConfigurationResource.exists()) {
            LOGGER.info("Found logback configuration " + logbackConfigurationResource.getDescription()
                    + " - Overriding default configuration");
            JoranConfigurator configurator = new JoranConfigurator();
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            configurator.setContext(loggerContext);
            try {
                configurator.doConfigure(logbackConfigurationResource.getInputStream());
                LOGGER.info("default configuration overrided by logback configuration "
                        + logbackConfigurationResource.getDescription());
            } catch (Exception exception) {
                try {
                    new ContextInitializer(loggerContext).autoConfig();
                } catch (JoranException e) {
                    BasicConfigurator.configureDefaultContext();
                    LOGGER.error("Can't configure default configuration", exception);
                }
                LOGGER.error(
                        "Can't configure logback with specified " + logbackConfigurationResource.getDescription()
                                + " - Keep default configuration", exception);
            }
        } else {
            LOGGER.error("Can't read logback configuration specified file at "
                    + logbackConfigurationResource.getDescription() + " - Keep default configuration");
        }
    }

    private Resource getConfigurationResource(String logbackConfigurationUri) {
        Resource logbackConfigurationResource;
        if (logbackConfigurationUri.startsWith(CLASSPATH)) {
            logbackConfigurationResource = new ClassPathResource(logbackConfigurationUri.substring(CLASSPATH.length()));
        } else {
            logbackConfigurationResource = new FileSystemResource(logbackConfigurationUri);
        }
        return logbackConfigurationResource;
    }

    private String getConfigurationUri(ServletContextEvent servletContextEvent) {
        String defaultUri = CLASSPATH + "logback.xml";
        String logbackConfigParameter = servletContextEvent.getServletContext().getInitParameter("logbackConfigParameter");
        if (logbackConfigParameter != null && !logbackConfigParameter.isEmpty()) {
            return System.getProperty(logbackConfigParameter, defaultUri);
        } else {
            return defaultUri;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
