package se.inera.statistics.service.processlog;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.*;

import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.queue.Receiver;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/process-log-qm-test.xml", "/process-log-impl-test.xml" })
public class ReceiverQueueFunctionalTest {

    private JmsTemplate jmsTemplate;

    @Before
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
        setConnectionFactory(cf);
    }

    public void setConnectionFactory(ConnectionFactory cf) {
        this.jmsTemplate = new JmsTemplate(cf);
    }

    public void simpleSend() {
        Destination destination = new ActiveMQQueue("intyg.queue");

        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage("hello queue world");
                message.setJMSCorrelationID("C12");
                return message;
            }
        });
    }

    /**
     * Functional test to check if a message is consumed. The proof is in the
     * pudding. Check output for "Received intyg" and inspect its
     * representation.
     */
    @Ignore
    @Test
    public void send() {
        simpleSend();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
