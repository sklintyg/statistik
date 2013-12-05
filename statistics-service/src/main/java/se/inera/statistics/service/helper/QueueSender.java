package se.inera.statistics.service.helper;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.queue.Receiver;

public class QueueSender {
    private JmsTemplate jmsTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Queue destination;

    @PostConstruct
    public void init() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    public void simpleSend(final String intyg, final String correlationId) {

        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setStringProperty(Receiver.ACTION, Receiver.CREATED);
                message.setStringProperty(Receiver.CERTIFICATE_ID, correlationId);
                return message;
            }
        });
    }

    public void simpleSend(final String intyg, final String correlationId, final EventType type) {

        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setStringProperty(Receiver.ACTION, type.name());
                message.setStringProperty(Receiver.CERTIFICATE_ID, correlationId);
                return message;
            }
        });
    }

}
