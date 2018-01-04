/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.testsupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.queue.JmsReceiver;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.TextMessage;

public class QueueSender {
    private JmsTemplate jmsTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Queue destination;

    @PostConstruct
    public void init() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    public void simpleSend(final String intyg, final String correlationId, String certificateType) {
        simpleSend(intyg, correlationId, JmsReceiver.CREATED, certificateType);
    }

    public void simpleSend(final String intyg, final String correlationId, final EventType type, String certificateType) {
        simpleSend(intyg, correlationId, type.name(), certificateType);
    }

    private void simpleSend(String intyg, String correlationId, String action, String certificateType) {
        jmsTemplate.send(destination, session -> {
            TextMessage message = session.createTextMessage(intyg);
            message.setStringProperty(JmsReceiver.ACTION, action);
            message.setStringProperty(JmsReceiver.CERTIFICATE_TYPE, certificateType);
            message.setStringProperty(JmsReceiver.CERTIFICATE_ID, correlationId);
            return message;
        });

    }

}
