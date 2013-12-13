package se.inera.statistics.service.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.ProcessLog;

import com.fasterxml.jackson.databind.JsonNode;

public class Receiver implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    public static final String CREATED = "created";
    public static final String REVOKED = "revoked";
    public static final String ACTION = "action";
    public static final String CERTIFICATE_ID = "certificate-id";
    @Autowired
    private ProcessLog processLog;

    @Autowired
    private HSADecorator hsaDecorator;

    private long accepted;

    public void accept(EventType type, String data, String documentId, long timestamp) {
        processLog.store(type, data, documentId, timestamp);
        JsonNode utlatande = JSONParser.parse(data);
        hsa(documentId, utlatande);
        accepted++;
    }

    public long getAccepted() {
        return accepted;
    }

    private void hsa(String documentId, JsonNode utlatande) {
        try {
            hsaDecorator.decorate(utlatande, documentId);
        } catch (Exception e) {
            LOG.error("Failed decorating intyg {}", documentId, e.getMessage());
        }
    }

    public void onMessage(Message rawMessage) {
        if (rawMessage instanceof TextMessage) {
            try {
                String doc = ((TextMessage) rawMessage).getText();
                long timestamp = rawMessage.getJMSTimestamp();
                String typeName = rawMessage.getStringProperty(ACTION);
                String certificateId = rawMessage.getStringProperty(CERTIFICATE_ID);
                accept(typeEvent(typeName), doc, certificateId, timestamp);
                LOG.info("Received intyg {}", certificateId);
            } catch (JMSException e) {
                throw new StatisticsJMSException("JMS error", e);
            }
        } else {
            LOG.error("Unrecognized message type " + rawMessage.getClass().getCanonicalName());
        }
        LOG.debug("Received intyg " + rawMessage + " .");
    }

    public void setProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
    }

    private static EventType typeEvent(String typeName) {
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
