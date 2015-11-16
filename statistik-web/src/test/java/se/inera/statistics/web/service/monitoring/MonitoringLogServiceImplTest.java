/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service.monitoring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringLogServiceImplTest {
    
    private static final String FILE_NAME = "FILE_NAME";
    private static final int ROWS = 99;
    private static final HsaIdUser HSA_USER = new HsaIdUser("HSA_USER");
    private static final HsaIdVardgivare HSA_VARDGIVAR = new HsaIdVardgivare("HSA_VARDGIVAR");
    private static final HsaIdEnhet HSA_ENHET = new HsaIdEnhet("HSA_ENHET");
    
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
    
    MonitoringLogService logService = new MonitoringLogServiceImpl();

    @Before
    public void setup() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }
    
    @After
    public void teardown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }
    
    @Test
    public void shouldLogUserLogin() {
        logService.logUserLogin(HSA_USER, HSA_VARDGIVAR, HSA_ENHET);
        verifyLog(Level.INFO, "USER_LOGIN Login user hsaId 'HSA_USER', vardgivarId 'HSA_VARDGIVAR', vardenhetsId 'HSA_ENHET'");
    }

    @Test
    public void shouldLogFileUpload() {
        logService.logFileUpload(HSA_USER, HSA_VARDGIVAR, FILE_NAME, ROWS);
        verifyLog(Level.INFO, "FILE_UPLOAD User hsaId 'HSA_USER', vardgivarId 'HSA_VARDGIVAR' uploaded file 'FILE_NAME' with '99' rows");
    }

    private void verifyLog(Level logLevel, String logMessage) {
        // Verify and capture logging interaction
        verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();

        // Verify log
        assertThat(loggingEvent.getLevel(), equalTo(logLevel));
        assertThat(loggingEvent.getFormattedMessage(), 
                equalTo(logMessage));
    }
}
