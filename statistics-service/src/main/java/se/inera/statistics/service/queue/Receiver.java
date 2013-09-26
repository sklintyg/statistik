package se.inera.statistics.service.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.ProcessLog;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class Receiver implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private ProcessLog processLog;

    public void accept(EventType type, String data, String documentId, long timestamp) {
        processLog.store(type, data, documentId, timestamp);
    }

    public void onMessage(Message rawData) {
        if (rawData instanceof TextMessage) {
            try {
                String doc = ((TextMessage) rawData).getText();
                CorrelationId correlationId = new CorrelationId(rawData.getJMSCorrelationID());
                long timestamp = rawData.getJMSTimestamp();
                accept(correlationId.eventType, doc, correlationId.correlationId, timestamp);
            } catch (JMSException e) {
                throw new StatisticsJMSException("JMS error", e);
            }
        } else {
            LOG.error("Unrecognized message type " + rawData.getClass().getCanonicalName());
        }
        LOG.debug("Received intyg " + rawData + " .");
    }

    public void setProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
    }

    private class CorrelationId {
        private String correlationId;
        private EventType eventType;

        public CorrelationId(String jmsCorrelationID) {
            if (jmsCorrelationID == null || jmsCorrelationID.length() < 2) {
                throw new StatisticsCorrelationIdException("Correlation id wrong format: " + jmsCorrelationID);
            }
            char typeHeader = jmsCorrelationID.charAt(0);

            switch (typeHeader) {
            case 'C':
                eventType = EventType.CREATED;
                break;
            case 'D':
                eventType = EventType.DELETED;
                break;
            default:
                eventType = EventType.TEST;
            }
            correlationId = jmsCorrelationID;
        }
    }
}
