package se.inera.statistics.service.processlog;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

import javax.jms.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/process-log-qm-test.xml" })
public class ReceiverQueueTest {

    private ProcessLog processLog = Mockito.mock(ProcessLog.class);

    private Receiver receiver = new Receiver();

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
                return session.createTextMessage("hello queue world");
            }
        });
    }

    /**
     *
     */
    @Test
    public void send() {
        simpleSend();
    }

}
