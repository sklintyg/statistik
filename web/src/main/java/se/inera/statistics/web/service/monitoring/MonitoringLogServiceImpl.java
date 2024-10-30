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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.auth.LoginMethod;
import se.inera.intyg.infra.monitoring.logging.MarkerFilter;
import se.inera.intyg.statistik.logging.MdcCloseableMap;
import se.inera.intyg.statistik.logging.MdcLogConstants;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

@Slf4j
@Service("webMonitoringLogService")
public class MonitoringLogServiceImpl implements MonitoringLogService {

    private static final Object SPACE = " ";

    @Override
    public void logUserLogin(HsaIdUser hsaUser, LoginMethod loginMethod) {
        String hsaUserId = hsaUser != null ? hsaUser.getId() : null;
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.USER_LOGIN))
                .put(MdcLogConstants.USER_ID, getValue(hsaUserId))
                .put(MdcLogConstants.EVENT_LOGIN_METHOD, loginMethod.value())
                .build()
        ) {
            logEvent(MonitoringEvent.USER_LOGIN, hsaUserId, loginMethod.value());
        }
    }

    @Override
    public void logUserLoginFailed(String exception) {
        logEvent(MonitoringEvent.USER_LOGIN_FAILURE, exception);
    }

    @Override
    public void logFileUpload(HsaIdUser hsaUser, HsaIdVardgivare hsaVardgivare, String fileName, Integer rows) {
        String hsaUserId = hsaUser != null ? hsaUser.getId() : null;
        String vardgivarId = hsaVardgivare != null ? hsaVardgivare.getId() : null;
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.FILE_UPLOAD))
                .put(MdcLogConstants.USER_ID, getValue(hsaUserId))
                .put(MdcLogConstants.EVENT_USER_CARE_PROVIDER_ID, getValue(vardgivarId))
                .build()
        ) {
            logEvent(MonitoringEvent.FILE_UPLOAD, hsaUserId, vardgivarId, fileName, rows);
        }
    }

    @Override
    public void logTrackAccessProtectedChartData(HsaIdUser hsaUser, HsaIdVardgivare hsaVardgivare, String uri) {
        String hsaUserId = hsaUser != null ? hsaUser.getId() : null;
        String vardgivarId = hsaVardgivare != null ? hsaVardgivare.getId() : null;
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.TRACK_ACCESS_PROTECTED_CHART_DATA))
                .put(MdcLogConstants.USER_ID, getValue(hsaUserId))
                .put(MdcLogConstants.EVENT_USER_CARE_PROVIDER_ID, getValue(vardgivarId))
                .build()
        ) {
            logEvent(MonitoringEvent.TRACK_ACCESS_PROTECTED_CHART_DATA, hsaUserId, vardgivarId, uri);
        }
    }

    @Override
    public void logTrackAccessAnonymousChartData(String uri) {
        logEvent(MonitoringEvent.TRACK_ACCESS_ANONYMOUS_CHART_DATA, uri);
    }

    @Override
    public void logBrowserInfo(String browserName, String browserVersion, String osFamily, String osVersion, String width, String height) {
        logEvent(MonitoringEvent.BROWSER_INFO, browserName, browserVersion, osFamily, osVersion, width, height);
    }

    private void logEvent(MonitoringEvent logEvent, Object... logMsgArgs) {
        log.info(MarkerFilter.MONITORING, buildMessage(logEvent), logMsgArgs);
    }

    private String buildMessage(MonitoringEvent logEvent) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
        return logMsg.toString();
    }

    private enum MonitoringEvent {
        USER_LOGIN("Login user hsaId '{}' using login method '{}'"),
        FILE_UPLOAD("User hsaId '{}', vardgivarId '{}' uploaded file '{}' with '{}' rows"),
        TRACK_ACCESS_PROTECTED_CHART_DATA("User hsaId '{}', vardgivarId '{}' accessed uri '{}'"),
        TRACK_ACCESS_ANONYMOUS_CHART_DATA("Accessed uri '{}'"),
        BROWSER_INFO("Name '{}' Version '{}' OSFamily '{}' OSVersion '{}' Width '{}' Height '{}'"),
        USER_LOGIN_FAILURE(
            "User failed to login, exception message '{}'");

        private final String message;

        MonitoringEvent(String msg) {
            this.message = msg;
        }

        public String getMessage() {
            return message;
        }
    }

    private String toEventType(MonitoringEvent monitoringEvent) {
        return monitoringEvent.name().toLowerCase().replace("_", "-");
    }


    private static String getValue(String value) {
        return value != null ? value : "-";
    }
}