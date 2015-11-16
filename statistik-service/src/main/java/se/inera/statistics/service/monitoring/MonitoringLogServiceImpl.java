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
package se.inera.statistics.service.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.inera.certificate.logging.LogMarkers;

@Service("serviceMonitoringLogService")
public class MonitoringLogServiceImpl implements MonitoringLogService {

    private static final Object SPACE = " ";
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringLogService.class);

    @Override
    public void logCertificateReceivedFromQueue(String certificateId) {
        logEvent(MonitoringEvent.CERTIFICATE_RECEIVED_FROM_QUEUE, certificateId);
    }
    
    @Override
    public void logCertificateBatchProcessedFromTable(int nbrCertificates) {
        logEvent(MonitoringEvent.CERTIFICATE_BATCH_PROCESSED_FROM_TABLE, nbrCertificates);
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
        CERTIFICATE_RECEIVED_FROM_QUEUE("Received certificateId '{}' from queue"),
        CERTIFICATE_BATCH_PROCESSED_FROM_TABLE("Processed batch with '{}' certificates");
    	
        private String message;

        private MonitoringEvent(String msg) {
            this.message = msg;
        }

        public String getMessage() {
            return message;
        }
    }
}
