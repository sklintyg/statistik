/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import se.inera.statistics.service.monitoring.MonitoringLogService;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.Receiver;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class JmsReceiver implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(JmsReceiver.class);

    public static final String CREATED = "created";
    public static final String REVOKED = "revoked";
    public static final String ACTION = "action";
    public static final String CERTIFICATE_ID = "certificate-id";
    public static final String MESSAGE_SENT = "message-sent";

    @Autowired
    private Receiver receiver;

    @Autowired
    @Qualifier("serviceMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Override
    public void onMessage(Message rawMessage) {
        if (rawMessage instanceof TextMessage) {
            try {
                String doc = ((TextMessage) rawMessage).getText();
                long timestamp = rawMessage.getJMSTimestamp();
                String typeName = rawMessage.getStringProperty(ACTION);

                // INTYG-3515
                if (!MESSAGE_SENT.equals(typeName)) {
                    String certificateId = rawMessage.getStringProperty(CERTIFICATE_ID);
                    receiver.accept(typeEvent(typeName), doc, certificateId, timestamp);
                    LOG.info("Received intyg {}", certificateId);
                    monitoringLogService.logInFromQueue(certificateId);
                }
            } catch (JMSException e) {
                throw new StatisticsJMSException("JMS error", e);
            }
        } else {
            LOG.error("Unrecognized message type " + rawMessage.getClass().getCanonicalName());
        }
        LOG.debug("Received intyg " + rawMessage + " .");
    }

    private static EventType typeEvent(String typeName) {
        if (typeName == null) {
            LOG.error("Type should not be null for message");
            return EventType.TEST;
        }
        switch (typeName.toLowerCase()) {
        case CREATED:
            return EventType.CREATED;
        case REVOKED:
            return EventType.REVOKED;
        default:
            return EventType.TEST;
        }
    }
}
