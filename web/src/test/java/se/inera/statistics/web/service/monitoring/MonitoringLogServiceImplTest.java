/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import se.inera.auth.LoginMethod;
import se.inera.intyg.statistik.logging.MdcLogConstants;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

@ExtendWith(MockitoExtension.class)
class MonitoringLogServiceImplTest {

    private static final int ROWS = 99;

    private static final String FILE_NAME = "FILE_NAME";
    private static final String URI = "URI";

    private static final String USER_ID = "HSA_USER";
    private static final HsaIdUser HSA_USER = new HsaIdUser(USER_ID);
    private static final String CARE_PROVIDER_ID = "HSA_VARDGIVARE";
    private static final HsaIdVardgivare HSA_VARDGIVARE = new HsaIdVardgivare(CARE_PROVIDER_ID);

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    MonitoringLogService logService = new MonitoringLogServiceImpl();

    @BeforeEach
    public void setup() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }

    @AfterEach
    public void teardown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }

    @Nested
    class LogUserLoginTests {

        @Test
        void shouldLogUserLogin() {
            logService.logUserLogin(HSA_USER, LoginMethod.SITHS);
            verifyLog(Level.INFO, "USER_LOGIN Login user hsaId 'HSA_USER' using login method 'SITHS'");
        }

        @Test
        void shouldIncludeUserHsaIdInMDC() {
            logService.logUserLogin(HSA_USER, LoginMethod.SITHS);
            assertEquals(USER_ID, getValueFromMDC(MdcLogConstants.USER_ID));
        }

        @Test
        void shouldIncludeLoginMethodInMDC() {
            logService.logUserLogin(HSA_USER, LoginMethod.SITHS);
            assertEquals(LoginMethod.SITHS.name(), getValueFromMDC(MdcLogConstants.EVENT_LOGIN_METHOD));
        }
    }


    @Test
    void shallLogUserLoginFailed() {
        final var exception = "exception";
        logService.logUserLoginFailed(exception);
        verifyLog(Level.INFO, String.format("USER_LOGIN_FAILURE User failed to login, exception message '%s'", exception));
    }

    @Nested
    class LogFileUploadTests {

        @Test
        void shouldLogFileUpload() {
            logService.logFileUpload(HSA_USER, HSA_VARDGIVARE, FILE_NAME, ROWS);
            verifyLog(Level.INFO,
                "FILE_UPLOAD User hsaId 'HSA_USER', vardgivarId 'HSA_VARDGIVARE' uploaded file 'FILE_NAME' with '99' rows");
        }

        @Test
        void shouldIncludeUserHsaIdInMDC() {
            logService.logFileUpload(HSA_USER, HSA_VARDGIVARE, FILE_NAME, ROWS);
            assertEquals(USER_ID, getValueFromMDC(MdcLogConstants.USER_ID));
        }

        @Test
        void shouldIncludeCareGiverIdInMDC() {
            logService.logFileUpload(HSA_USER, HSA_VARDGIVARE, FILE_NAME, ROWS);
            assertEquals(CARE_PROVIDER_ID, getValueFromMDC(MdcLogConstants.EVENT_USER_CARE_PROVIDER_ID));
        }
    }

    @Nested
    class LogTrackAccessProtectedChartDataTests {

        @Test
        void shouldLogTrackAccessProtectedChartData() {
            logService.logTrackAccessProtectedChartData(HSA_USER, HSA_VARDGIVARE, URI);
            verifyLog(Level.INFO,
                "TRACK_ACCESS_PROTECTED_CHART_DATA User hsaId 'HSA_USER', vardgivarId 'HSA_VARDGIVARE' accessed uri 'URI'");
        }


        @Test
        void shouldIncludeUserHsaIdInMDC() {
            logService.logTrackAccessProtectedChartData(HSA_USER, HSA_VARDGIVARE, URI);
            assertEquals(USER_ID, getValueFromMDC(MdcLogConstants.USER_ID));
        }

        @Test
        void shouldIncludeCareGiverIdInMDC() {
            logService.logTrackAccessProtectedChartData(HSA_USER, HSA_VARDGIVARE, URI);
            assertEquals(CARE_PROVIDER_ID, getValueFromMDC(MdcLogConstants.EVENT_USER_CARE_PROVIDER_ID));
        }

    }


    @Test
    void shouldLogTrackAccessAnonymousChartData() {
        logService.logTrackAccessAnonymousChartData(URI);
        verifyLog(Level.INFO, "TRACK_ACCESS_ANONYMOUS_CHART_DATA Accessed uri 'URI'");
    }

    @Test
    void shouldLogBrowserInfo() {
        logService.logBrowserInfo("BROWSERNAME", "VERSION", "OS", "OS-VERSION", "WIDTH", "HEIGHT");
        verifyLog(Level.INFO,
            "BROWSER_INFO Name 'BROWSERNAME' Version 'VERSION' OSFamily 'OS' OSVersion 'OS-VERSION' Width 'WIDTH' Height 'HEIGHT'");
    }

    private void verifyLog(Level logLevel, String logMessage) {
        // Verify and capture logging interaction
        verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();

        // Verify log
        assertEquals(loggingEvent.getLevel(), logLevel);
        assertEquals(loggingEvent.getFormattedMessage(), logMessage);
    }

    private String getValueFromMDC(String key) {
        verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final var loggingEvent = captorLoggingEvent.getValue();
        return loggingEvent.getMDCPropertyMap().get(key);
    }
}