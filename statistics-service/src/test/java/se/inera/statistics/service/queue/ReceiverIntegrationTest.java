package se.inera.statistics.service.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.CasesPerMonthRow;

import javax.jms.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/process-log-qm-test.xml", "/process-log-impl-test.xml" })
public class ReceiverIntegrationTest {

    private JmsTemplate jmsTemplate;

    @Autowired
    private CasesPerMonth casesPerMonth;

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
        this.jmsTemplate = new JmsTemplate(cf);
    }

    @Test
    public void deliver_document_from_in_queue_to_statistics_repository() {
        String data = JSONSource.readTemplateAsString().toString();

        simpleSend(data, "B123");

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        List<CasesPerMonthRow> webData = casesPerMonth.getCasesPerMonth();

        assertEquals(1, webData.size());

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

}
