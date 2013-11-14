package se.inera.statistics.service.queue;

import static org.junit.Assert.assertEquals;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.demo.UtlatandeBuilder;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.listener.AldersGruppListener;
import se.inera.statistics.service.report.listener.SjukfallPerDiagnosgruppListener;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:process-log-qm-test.xml" })
@Transactional
@DirtiesContext
public class ReceiverIntegrationTest {

    private JmsTemplate jmsTemplate;

    @Autowired
    private CasesPerMonth casesPerMonth;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private LogConsumer consumer;

    @Before
    public void setup() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    @Test
    public void deliver_document_from_in_queue_to_statistics_repository() {
        AldersGruppListener.setMaxCacheSize(1);
        SjukfallPerDiagnosgruppListener.setMaxCacheSize(1);
        UtlatandeBuilder builder = new UtlatandeBuilder();
        simpleSend(builder.build("19121212-0010", new LocalDate("2011-01-20"), new LocalDate("2011-03-11"), "enhetId", "A00", 0).toString(), "C001");
        simpleSend(builder.build("19121212-0011", new LocalDate("2011-01-20"), new LocalDate("2011-03-11"), "enhetId", "A00", 0).toString(), "C002");

        sleep();

        assertEquals(2, consumer.processBatch());

        SimpleDualSexResponse<SimpleDualSexDataRow> webData = casesPerMonth.getCasesPerMonth("enhetId", new Range(new LocalDate("2011-01"), new LocalDate("2011-12")));

        assertEquals(12, webData.getRows().size());

        for (int i = 0; i < 3; i++) {
            assertEquals(0, webData.getRows().get(i).getFemale().intValue());
            assertEquals(2, webData.getRows().get(i).getMale().intValue());
        }
        for (int i = 3; i < 12; i++) {
            assertEquals(0, webData.getRows().get(i).getFemale().intValue());
            assertEquals(0, webData.getRows().get(i).getMale().intValue());
        }
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
//CHECKSTYLE:ON MagicNumber
