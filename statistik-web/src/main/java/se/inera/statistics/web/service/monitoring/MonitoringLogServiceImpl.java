/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.monitoring.LogMarkers;

@Service("webMonitoringLogService")
public class MonitoringLogServiceImpl implements MonitoringLogService {

    private static final Object SPACE = " ";
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringLogService.class);

    @Override
    public void logUserLogin(HsaIdUser hsaUser) {
      String hsaUserId = hsaUser != null ? hsaUser.getId() : null;
      logEvent(MonitoringEvent.USER_LOGIN, hsaUserId);
    }

    @Override
    public void logFileUpload(HsaIdUser hsaUser, HsaIdVardgivare hsaVardgivare, String fileName, Integer rows) {
        String hsaUserId = hsaUser != null ? hsaUser.getId() : null;
        String vardgivarId = hsaVardgivare != null ? hsaVardgivare.getId() : null;

        logEvent(MonitoringEvent.FILE_UPLOAD, hsaUserId, vardgivarId, fileName, rows);
    }

    @Override
    public void logTrackAccessProtectedChartData(HsaIdUser hsaUser, HsaIdVardgivare hsaVardgivare, String uri) {
        String hsaUserId = hsaUser != null ? hsaUser.getId() : null;
        String vardgivarId = hsaVardgivare != null ? hsaVardgivare.getId() : null;

        logEvent(MonitoringEvent.TRACK_ACCESS_PROTECTED_CHART_DATA, hsaUserId, vardgivarId, uri);
    }

    @Override
    public void logTrackAccessAnonymousChartData(String uri) {
        logEvent(MonitoringEvent.TRACK_ACCESS_ANONYMOUS_CHART_DATA, uri);
    }

    private void logEvent(MonitoringEvent logEvent, Object... logMsgArgs) {
        LOG.info(LogMarkers.MONITORING, buildMessage(logEvent), logMsgArgs);
    }

    private String buildMessage(MonitoringEvent logEvent) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
        return logMsg.toString();
    }

    private enum MonitoringEvent {
        USER_LOGIN("Login user hsaId '{}'"),
        FILE_UPLOAD("User hsaId '{}', vardgivarId '{}' uploaded file '{}' with '{}' rows"),
        TRACK_ACCESS_PROTECTED_CHART_DATA("User hsaId '{}', vardgivarId '{}' accessed uri '{}'"),
        TRACK_ACCESS_ANONYMOUS_CHART_DATA("Accessed uri '{}'");

        private String message;

        MonitoringEvent(String msg) {
            this.message = msg;
        }

        public String getMessage() {
            return message;
        }
    }
}
