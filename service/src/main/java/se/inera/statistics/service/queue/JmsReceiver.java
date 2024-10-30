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
package se.inera.statistics.service.queue;

import static se.inera.intyg.statistik.logging.MdcLogConstants.SPAN_ID_KEY;
import static se.inera.intyg.statistik.logging.MdcLogConstants.TRACE_ID_KEY;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import se.inera.intyg.statistik.logging.MdcCloseableMap;
import se.inera.intyg.statistik.logging.MdcHelper;
import se.inera.statistics.service.monitoring.MonitoringLogService;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.Receiver;
import se.inera.statistics.service.processlog.intygsent.ProcessIntygsentLog;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.processlog.message.ProcessMessageLog;
import se.inera.statistics.service.warehouse.IntygType;

public class JmsReceiver implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(JmsReceiver.class);

    public static final String CREATED = "created";
    public static final String REVOKED = "revoked";
    public static final String ACTION = "action";
    public static final String ACTION_TYPE_MESSAGE_SENT = "message-sent";
    public static final String ACTION_TYPE_CERTIFICATE_SENT = "sent";
    public static final String CERTIFICATE_ID = "certificate-id";
    public static final String MESSAGE_ID = "message-id";
    public static final String CERTIFICATE_TYPE = "certificate-type";
    public static final String CERTIFICATE_SENT_RECIPIENT = "certificate-recipient";

    @Autowired
    private Receiver receiver;

    @Autowired
    private ProcessMessageLog processMessageLog;

    @Autowired
    private ProcessIntygsentLog processIntygsentLog;

    @Autowired
    @Qualifier("serviceMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Autowired
    private MdcHelper mdcHelper;

    @Override
    public void onMessage(Message rawMessage) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(TRACE_ID_KEY, mdcHelper.traceId())
                .put(SPAN_ID_KEY, mdcHelper.spanId())
                .build()
        ) {
            if (rawMessage instanceof TextMessage) {
                try {
                    String doc = ((TextMessage) rawMessage).getText();
                    long timestamp = rawMessage.getJMSTimestamp();
                    String typeName = rawMessage.getStringProperty(ACTION);

                    if (ACTION_TYPE_MESSAGE_SENT.equals(typeName)) {
                        handleMessage(rawMessage, doc, typeName, timestamp);
                    } else if (ACTION_TYPE_CERTIFICATE_SENT.equals(typeName)) {
                        handleIntygSent(rawMessage, timestamp);
                    } else {
                        handleIntyg(rawMessage, doc, typeName, timestamp);
                    }
                } catch (JMSException e) {
                    throw new StatisticsJMSException("JMS error", e);
                }
            } else {
                LOG.error("Unrecognized message type " + rawMessage.getClass().getCanonicalName());
            }
            LOG.debug("Received intyg " + rawMessage + " .");
        }
    }

    private void handleIntygSent(Message rawMessage, long timestamp) throws JMSException {
        String certificateId = rawMessage.getStringProperty(CERTIFICATE_ID);
        String certificateRecipient = rawMessage.getStringProperty(CERTIFICATE_SENT_RECIPIENT);
        processIntygsentLog.store(certificateId, certificateRecipient, timestamp);
        LOG.info("Received intyg sent event for {} to {}", certificateId, certificateRecipient);
        monitoringLogService.logInFromQueue(certificateId);
    }

    private void handleIntyg(Message rawMessage, String doc, String typeName, long timestamp) throws JMSException {
        String certificateId = rawMessage.getStringProperty(CERTIFICATE_ID);
        String certificateType = rawMessage.getStringProperty(CERTIFICATE_TYPE);
        if (certificateType != null && IntygType.getByItIntygType(certificateType).equals(IntygType.UNKNOWN)) {
            LOG.warn("Received intyg '{}' was discarded since type '{}' is unknown", certificateId, certificateType);
        } else {
            receiver.accept(typeEvent(typeName), doc, certificateId, timestamp);
            LOG.info("Received intyg {}", certificateId);
            monitoringLogService.logInFromQueue(certificateId);
        }
    }

    private void handleMessage(Message rawMessage, String doc, String typeName, long timestamp) throws JMSException {
        String messageId = rawMessage.getStringProperty(MESSAGE_ID);
        processMessageLog.store(typeMessageEvent(typeName), doc, messageId, timestamp);
        LOG.info("Received message {}", messageId);
        monitoringLogService.logInFromQueue(messageId);
    }

    private static MessageEventType typeMessageEvent(String typeName) {
        if (typeName == null) {
            LOG.error("Type should not be null for message");
            return MessageEventType.SENT;
        }
        switch (typeName.toLowerCase()) {
            case ACTION_TYPE_MESSAGE_SENT:
                return MessageEventType.SENT;
            default:
                return MessageEventType.SENT;
        }
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