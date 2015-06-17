/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.queue;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.hsa.model.HsaId;
import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.processlog.LogConsumer;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.junit.Assert.assertEquals;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:process-log-qm-test.xml", "classpath:icd10.xml" })
@DirtiesContext
@ActiveProfiles("transactional-test-mock")
public class ReceiverTransactionalTest {

    private static final int PERSON_K1950 = 0;

    public static final int ENVE = 0;
    public static final int TVAVE = 1;

    public static final String ACTIVEMQ_DLQ = "ActiveMQ.DLQ";

    private static final Logger LOG = LoggerFactory.getLogger(ReceiverTransactionalTest.class);
    private List<String> persons = new ArrayList<>();

    private JmsTemplate jmsTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private LogConsumer consumer;


    @Autowired
    private se.inera.statistics.service.testsupport.QueueSender queueSender;

    @Before
    public void setup() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
        List<String> personNummers;
        personNummers = readList("/personnr/testpersoner.log");
        persons.add(personNummers.get(0)); // k 1950
    }

    @Test
    public void unprocessable_document_should_end_up_on_DLQ_because_we_use_transactions() throws IOException {
        UtlatandeBuilder builder = new UtlatandeBuilder("/json/integration/intyg1.json");

        assertEquals("Verify no messages on DLQ before we test", 0, getMessagesInQueue(ACTIVEMQ_DLQ));

        int i = 0;
        for (TestIntyg intyg : getIntygWithHelsjukAndSingelmanadAndSingelDiagnos(getPerson(PERSON_K1950))) {
            LOG.info("Intyg: " + intyg);
            queueSender.simpleSend(builder.build(intyg.personNr, intyg.startDate, intyg.endDate, intyg.vardenhet, intyg.vardgivare, intyg.diagnos, intyg.grads).toString(),
                    "" + i++);
        }

        sleep();

        assertEquals("Verify that no messages have been processed.", 0, consumer.processBatch());

        assertEquals("Verify that unprocessable messages end up on DLQ", 1, getMessagesInQueue(ACTIVEMQ_DLQ));
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected int getMessagesInQueue(String queueName) {
        return jmsTemplate.browse(queueName, new BrowserCallback<Integer>() {
            @Override
            public Integer doInJms(Session session, QueueBrowser browser) throws JMSException {
                Enumeration<?> messages = browser.getEnumeration();
                int total = 0;
                while (messages.hasMoreElements()) {
                    messages.nextElement();
                    total++;
                }

                return total;
            }
        });
    }

    private List<String> readList(String path) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"))) {
            List<String> list = new ArrayList<>();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<TestIntyg> getIntygWithHelsjukAndSingelmanadAndSingelDiagnos(String... personNummers) {
        List<TestIntyg> testIntygs = new ArrayList<>();
        for (String person : personNummers) {
            testIntygs.add(new TestIntyg(person, getGrads(0), getStart(1), getStop(1), getVardenhet(0), getVardgivare(0), getDiagnosis(0)));
        }
        return testIntygs;
    }

    private String getPerson(int id) {
        return persons.get(id);
    }

    private HsaId getVardenhet(int i) {
        HsaId[] enheter = {new HsaId("ENVE"), new HsaId("TVAVE")};
        return enheter[i];
    }

    private LocalDate getStart(int i) {
        LocalDate[] dates = {new LocalDate("2012-02-01"), new LocalDate("2011-01-11")};
        return dates[i];
    }

    private LocalDate getStop(int i) {
        LocalDate[] dates = {new LocalDate("2012-01-22"), new LocalDate("2011-02-21"), new LocalDate("2011-03-11"), new LocalDate("2013-11-17")};
        return dates[i];
    }

    private List<String> getGrads(int... grads) {
        List<String> resultList = new ArrayList<>();
        for (int i : grads) {
            resultList.add(Integer.toString(i));
        }
        return resultList;
    }

    private String getDiagnosis(int i) {
        String[] diagnoser = {"A00"};
        return diagnoser[i];
    }

    private HsaId getVardgivare(int i) {
        HsaId[] givare = {new HsaId("Vardgivare")};
        return givare[i];
    }

    public static class TestIntyg {
        private final String personNr;
        private final List<String> grads;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final HsaId vardenhet;
        private final HsaId vardgivare;
        private final String diagnos;

        public TestIntyg(String personNr, List<String> grads, LocalDate startDate, LocalDate endDate, HsaId vardenhet, HsaId vardgivare, String diagnos) {
            this.personNr = personNr;
            this.grads = grads;
            this.startDate = startDate;
            this.endDate = endDate;
            this.vardenhet = vardenhet;
            this.vardgivare = vardgivare;
            this.diagnos = diagnos;
        }

        @Override
        public String toString() {
            return "TestIntyg{" + "personNr='" + personNr + '\'' + ", grads=" + grads + ", startDate=" + startDate + ", endDate=" + endDate + ", vardenhet='"
                    + vardenhet + '\'' + ", vardgivare='" + vardgivare + '\'' + ", diagnos='" + diagnos + '\'' + '}';
        }
    }
}
//CHECKSTYLE:ON MagicNumber
