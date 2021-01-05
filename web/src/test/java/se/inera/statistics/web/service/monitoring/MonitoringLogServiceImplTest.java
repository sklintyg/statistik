/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.monitoring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
//import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringLogServiceImplTest {

    private static final int ROWS = 99;

    private static final String FILE_NAME = "FILE_NAME";
    private static final String URI = "URI";

    private static final HsaIdUser HSA_USER = new HsaIdUser("HSA_USER");
    private static final HsaIdVardgivare HSA_VARDGIVARE = new HsaIdVardgivare("HSA_VARDGIVARE");

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
        logService.logUserLogin(HSA_USER);
        verifyLog(Level.INFO, "USER_LOGIN Login user hsaId 'HSA_USER'");
    }

    @Test
    public void shouldLogFileUpload() {
        logService.logFileUpload(HSA_USER, HSA_VARDGIVARE, FILE_NAME, ROWS);
        verifyLog(Level.INFO, "FILE_UPLOAD User hsaId 'HSA_USER', vardgivarId 'HSA_VARDGIVARE' uploaded file 'FILE_NAME' with '99' rows");
    }

    @Test
    public void shouldLogTrackAccessProtectedChartData() {
        logService.logTrackAccessProtectedChartData(HSA_USER, HSA_VARDGIVARE, URI);
        verifyLog(Level.INFO, "TRACK_ACCESS_PROTECTED_CHART_DATA User hsaId 'HSA_USER', vardgivarId 'HSA_VARDGIVARE' accessed uri 'URI'");
    }

    @Test
    public void shouldLogTrackAccessAnonymousChartData() {
        logService.logTrackAccessAnonymousChartData(URI);
        verifyLog(Level.INFO, "TRACK_ACCESS_ANONYMOUS_CHART_DATA Accessed uri 'URI'");
    }

    @Test
    public void shouldLogBrowserInfo() {
        logService.logBrowserInfo("BROWSERNAME", "VERSION","OS", "OS-VERSION", "WIDTH", "HEIGHT");
        verifyLog(Level.INFO, "BROWSER_INFO Name 'BROWSERNAME' Version 'VERSION' OSFamily 'OS' OSVersion 'OS-VERSION' Width 'WIDTH' Height 'HEIGHT'");
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
