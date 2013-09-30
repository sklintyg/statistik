package se.inera.statistics.service.demo;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import se.inera.statistics.service.queue.Receiver;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.io.*;

public class InjectUtlatande {

    JmsTemplate jmsTemplate;

    public void setup() {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        try {
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
        this.jmsTemplate = new JmsTemplate(cf);
    }

    @PostConstruct
    public void init() {
        setup();
        publishUtlatande();
    }

    private void publishUtlatande() {

        String intyg = readTemplate("/json/fk7263_M_template.json");
        simpleSend(intyg, "C1123");
    }

    private void simpleSend(final String intyg, final String correlationId) {
        Destination destination = new ActiveMQQueue("intyg.queue");

        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setJMSCorrelationID(correlationId);
                return message;
            }
        });
    }

    private String readTemplate(String path) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path),"utf8"));
            StringBuilder sb = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
