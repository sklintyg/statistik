package se.inera.statistics.service.report.util;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import se.inera.statistics.service.demo.UtlatandeBuilder;
import se.inera.statistics.service.queue.Receiver;

// CHECKSTYLE:OFF MagicNumber
public class RemoteSender {

    private JmsTemplate jmsTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Queue destination;

    @PostConstruct
    public void setup() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    public void send() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        simpleSend(builder.build("20121212-1212", new LocalDate("2013-10-20"), new LocalDate("2013-11-11"), "TST5565594230-106J", "IFV1239877878-103H", "IFV1239877878-0001", "D01", 0).toString(), UUID.randomUUID().toString());
        simpleSend(builder.build("20121212-1212", new LocalDate("2013-10-20"), new LocalDate("2013-11-11"), "TST5565594230-106J", "IFV1239877878-103H", "IFV1239877878-0001", "INVALID", 0).toString(), UUID.randomUUID().toString());
        simpleSend(builder.build("20121262-1212", new LocalDate("2013-10-20"), new LocalDate("2013-11-11"), "TST5565594230-106J", "IFV1239877878-103H", "IFV1239877878-0001", "D01", 0).toString(), UUID.randomUUID().toString());
        simpleSend(builder.build("20126212-1212", new LocalDate("2013-10-20"), new LocalDate("2013-11-11"), "TST5565594230-106J", "IFV1239877878-103H", "IFV1239877878-0001", "D01", 0).toString(), UUID.randomUUID().toString());
        simpleSend(builder.build("20126212-1212", new LocalDate("2013-10-20"), new LocalDate("2013-11-11"), "TST5565594230-106J", longify("IFV1239877878-103H", 100), longify("IFV1239877878-0001", 100), "D01", 0).toString(), UUID.randomUUID().toString());
    }

    private String longify(String base, int length) {
        StringBuilder builder = new StringBuilder(base);
        builder.ensureCapacity(base.length() + length + 1);
        while (length -- > 0) {
            builder.append(" ");
        }
        builder.append(('S'));
        return builder.toString();
    }

    private void simpleSend(final String intyg, final String correlationId) {
        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setStringProperty(Receiver.ACTION, Receiver.CREATED);
                message.setStringProperty(Receiver.CERTIFICATE_ID, correlationId);
                return message;
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:send-to-mq-context.xml")) {
            RemoteSender bean = context.getBean("sender", RemoteSender.class);
            bean.connectionFactory = (ConnectionFactory) context.getBean("jmsFactory");
            bean.destination = (Queue) context.getBean("queue");
            bean.setup();
            bean.send();
            Thread.sleep(5000);
        }
    }
}
//CHECKSTYLE:ON MagicNumber
