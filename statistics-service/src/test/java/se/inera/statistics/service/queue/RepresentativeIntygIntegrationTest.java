package se.inera.statistics.service.queue;

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
import se.inera.statistics.service.scheduler.NationellUpdaterJob;

import javax.jms.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:process-log-qm-test.xml" })
@Transactional
@DirtiesContext
public class RepresentativeIntygIntegrationTest {

    private JmsTemplate jmsTemplate;

    @Autowired
    private CasesPerMonth casesPerMonth;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private NationellUpdaterJob nationellUpdaterJob;

    @Before
    public void setup() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    @Test
    public void deliver_document_from_in_queue_to_statistics_repository() throws IOException {
        AldersGruppListener.setMaxCacheSize(1);
        SjukfallPerDiagnosgruppListener.setMaxCacheSize(1);
        UtlatandeBuilder builder = new UtlatandeBuilder("/json/intyg1.json", "Intyg med 50%");
        List<String> personNummers = readList("/personnr/testpersoner.log");
        String person1 = personNummers.get(0);
        String person2 = personNummers.get(1000);

        simpleSend(builder.build(person1, new LocalDate("2011-01-20"), new LocalDate("2011-03-11"), "enhetId", "A00", 0).toString(), "001");
        simpleSend(builder.build(person2, new LocalDate("2011-01-20"), new LocalDate("2011-03-11"), "enhetId", "A00", 0).toString(), "002");

        sleep();

        assertEquals(2, consumer.processBatch());

        nationellUpdaterJob.checkLog();

        SimpleDualSexResponse<SimpleDualSexDataRow> webData = casesPerMonth.getCasesPerMonth("enhetId", new Range(new LocalDate("2011-01"), new LocalDate("2011-12")));

        assertEquals(12, webData.getRows().size());

        for (int i = 0; i < 3; i++) {
            assertEquals(2, webData.getRows().get(i).getFemale().intValue());
            assertEquals(0, webData.getRows().get(i).getMale().intValue());
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
                message.setStringProperty(Receiver.ACTION, Receiver.CREATED);
                message.setStringProperty(Receiver.CERTIFICATE_ID, correlationId);
                return message;
            }
        });
    }

    private List<String> readList(String path) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"));) {
            List<String> list = new ArrayList<>();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
//CHECKSTYLE:ON MagicNumber
