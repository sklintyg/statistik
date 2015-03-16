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
import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WarehouseManager;
import se.inera.statistics.service.warehouse.WidelineManager;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.junit.Assert.assertEquals;

// CHECKSTYLE:OFF MagicNumber
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:process-log-qm-test.xml", "classpath:icd10.xml" })
@DirtiesContext
public class ReceiverIntegrationTest {

    private JmsTemplate jmsTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private WidelineManager wideLine;

    @Autowired
    private WarehouseManager warehouseManager;

    @Autowired
    private Warehouse warehouse;

    private SjukfallUtil sjukfallUtil = new SjukfallUtil();

    @Autowired
    private SjukfallQuery sjukfallQuery;

    @Before
    public void setup() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    @Test
    public void deliver_document_from_in_queue_to_statistics_repository() {
        populate();
        load();
        SimpleKonResponse<SimpleKonDataRow> webData = sjukfallQuery.getSjukfall(warehouse.get("vardgivarId"), sjukfallUtil.createEnhetFilter("enhetId"), new LocalDate("2011-01"), 12, 1);

        assertEquals(12, webData.getRows().size());

        for (int i = 0; i < 3; i++) {
            assertEquals(0, webData.getRows().get(i).getFemale());
            assertEquals(2, webData.getRows().get(i).getMale());
        }
        for (int i = 3; i < 12; i++) {
            assertEquals(0, webData.getRows().get(i).getFemale());
            assertEquals(0, webData.getRows().get(i).getMale());
        }

        assertEquals(2, wideLine.count());
    }

    @Transactional
    public void load() {
        warehouseManager.loadEnhetAndWideLines();
    }

    @Transactional
    public void populate() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        simpleSend(builder.build("19121212-0010", new LocalDate("2011-01-20"), new LocalDate("2011-03-11"), "enhetId", "A00", 0).toString(), "001");
        simpleSend(builder.build("19121212-0110", new LocalDate("2011-01-20"), new LocalDate("2011-03-11"), "enhetId", "A00", 0).toString(), "002");

        sleep();

        assertEquals(2, consumer.processBatch());
    }

    private void sleep() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void simpleSend(final String intyg, final String correlationId) {
        Destination destination = new ActiveMQQueue("intyg.queue");

        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(intyg);
                message.setStringProperty(JmsReceiver.ACTION, JmsReceiver.CREATED);
                message.setStringProperty(JmsReceiver.CERTIFICATE_ID, correlationId);
                return message;
            }
        });
    }
}
//CHECKSTYLE:ON MagicNumber
