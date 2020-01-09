/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.report.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.queue.JmsReceiver;
import se.inera.statistics.service.testsupport.UtlatandeBuilder;

// CHECKSTYLE:OFF MagicNumber
public class RemoteSender {

    private JmsTemplate jmsTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Queue destination;

    private RandomValues personer;
    private RandomValues lakare;
    private RandomValues enheter;
    private RandomValues vardgivare;

    @PostConstruct
    public void setup() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    public void setPersoner(RandomValues personer) {
        this.personer = personer;
    }

    public void setVardgivare(RandomValues vardgivare) {
        this.vardgivare = vardgivare;
    }

    public void setEnheter(RandomValues enheter) {
        this.enheter = enheter;
    }

    public void setLakare(RandomValues lakare) {
        this.lakare = lakare;
    }


    public void send() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        simpleSend(builder
                .build("20121212-2212", LocalDate.parse("2013-10-20"), LocalDate.parse("2013-11-11"), new HsaIdLakare("TST5565595230-106J"),
                    new HsaIdEnhet("IFV1239877878-103H"), new HsaIdVardgivare("IFV1239877878-0001"), "D01", 0).toString(),
            UUID.randomUUID().toString());
        simpleSend(builder
                .build("20121212-2212", LocalDate.parse("2013-10-20"), LocalDate.parse("2013-11-11"), new HsaIdLakare("TST5565595230-106J"),
                    new HsaIdEnhet("IFV1239877878-103H"), new HsaIdVardgivare("IFV1239877878-0001"), "INVALID", 0).toString(),
            UUID.randomUUID().toString());
        simpleSend(builder
                .build("20121262-2212", LocalDate.parse("2013-10-20"), LocalDate.parse("2013-11-11"), new HsaIdLakare("TST5565595230-106J"),
                    new HsaIdEnhet("IFV1239877878-103H"), new HsaIdVardgivare("IFV1239877878-0001"), "D01", 0).toString(),
            UUID.randomUUID().toString());
        simpleSend(builder
                .build("20126212-2212", LocalDate.parse("2013-10-20"), LocalDate.parse("2013-11-11"), new HsaIdLakare("TST5565595230-106J"),
                    new HsaIdEnhet("IFV1239877878-103H"), new HsaIdVardgivare("IFV1239877878-0001"), "D01", 0).toString(),
            UUID.randomUUID().toString());
        simpleSend(builder
            .build("20126212-2212", LocalDate.parse("2013-10-20"), LocalDate.parse("2013-11-11"), new HsaIdLakare("TST5565595230-106J"),
                new HsaIdEnhet(longify("IFV1239877878-103H", 100)), new HsaIdVardgivare(longify("IFV1239877878-0001", 100)), "D01", 0)
            .toString(), UUID.randomUUID().toString());
    }

    private String longify(String base, int length) {
        StringBuilder builder = new StringBuilder(base);
        builder.ensureCapacity(base.length() + length + 1);
        while (length-- > 0) {
            builder.append(" ");
        }
        builder.append(('S'));
        return builder.toString();
    }

    public void sendRandom() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        for (int i = 0; i < 10; i++) {
            String personid = personer.getRandom();
            String lakarid = lakare.getRandom();
            String enhetid = enheter.getRandom();
            String vargivarid = vardgivare.getRandom();
            String uuid = UUID.randomUUID().toString();
            simpleSend(builder.build(personid, LocalDate.parse("2013-10-20"), LocalDate.parse("2013-11-11"), new HsaIdLakare(lakarid),
                new HsaIdEnhet(enhetid), new HsaIdVardgivare(vargivarid), "D01", 0).toString(), uuid);
            if (i % 100 == 0) {
                System.err.println(i);
            }
            ;
        }
    }

    private void simpleSend(final String intyg, final String correlationId) {
        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setStringProperty(JmsReceiver.ACTION, JmsReceiver.CREATED);
                message.setStringProperty(JmsReceiver.CERTIFICATE_ID, correlationId);
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

    public static class RandomValues {

        private static Random random = new Random();

        private Resource source;

        private List<String> values;

        public void setSource(Resource source) {
            this.source = source;
        }

        @PostConstruct
        public void setup() throws IOException {
            values = new ArrayList<String>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(source.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    values.add(line);
                }
            }
        }

        public String getRandom() {
            return values.get(random.nextInt(values.size()));
        }
    }

}
//CHECKSTYLE:ON MagicNumber
