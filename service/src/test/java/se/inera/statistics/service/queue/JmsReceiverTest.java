/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.queue;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.jms.Message;
import jakarta.jms.MessageNotWriteableException;
import java.io.IOException;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.statistik.logging.MdcHelper;
import se.inera.statistics.service.monitoring.MonitoringLogService;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.Receiver;
import se.inera.statistics.service.processlog.intygsent.ProcessIntygsentLog;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.processlog.message.ProcessMessageLog;

@ExtendWith(MockitoExtension.class)
 class JmsReceiverTest {

    @Mock
    private Receiver receiver;
    @Mock
    private MonitoringLogService monitoringLogService;
    @Mock
    private ProcessIntygsentLog processIntygsentLog;
    @Mock
    private ProcessMessageLog processMessageLog;
    @Mock
    private MdcHelper mdcHelper;
    @InjectMocks
    private JmsReceiver jmsReceiver;

    @BeforeEach
     void setUp()  {
        doReturn("traceId").when(mdcHelper).traceId();
        doReturn("sessionId").when(mdcHelper).spanId();
    }

    @Test
     void testOnMessageIntygUnknowType() throws MessageNotWriteableException, IOException {
        String intyg = "intyg";
        String correlationId = "123";
        String certificateType = "not_existing";

        Message rawMessage = getIntygMessage(intyg, correlationId, certificateType, JmsReceiver.CREATED);

        jmsReceiver.onMessage(rawMessage);

        verify(receiver, never())
            .accept(Mockito.any(EventType.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
    }

    @Test
     void testOnMessageIntygMissingType() throws MessageNotWriteableException, IOException {
        String intyg = "intyg";
        String correlationId = "123";
        String certificateType = null;

        Message rawMessage = getIntygMessage(intyg, correlationId, certificateType, JmsReceiver.CREATED);

        jmsReceiver.onMessage(rawMessage);

        verify(receiver, times(1))
            .accept(Mockito.any(EventType.class), Mockito.eq(intyg), Mockito.eq(correlationId), Mockito.anyLong());
    }

    @ParameterizedTest
    @ValueSource(strings = {"luae_na","fk7210","fk7472","fk3226","fk7809","ts8071","fk7427","fk7426","fk3221","fk7810"})
     void testOnMessageIntygKnowType(String certificateType) throws MessageNotWriteableException, IOException {
        String intyg = "intyg";
        String correlationId = "123";

        Message rawMessage = getIntygMessage(intyg, correlationId, certificateType, JmsReceiver.CREATED);

        jmsReceiver.onMessage(rawMessage);

        verify(receiver, times(1))
            .accept(Mockito.eq(EventType.CREATED), Mockito.eq(intyg), Mockito.eq(correlationId), Mockito.anyLong());
    }

    @ParameterizedTest
    @ValueSource(strings = {"luae_na","fk7210","fk7472","fk3226","fk7809","ts8071","fk7427","fk7426","fk3221","fk7810"})
    void testOnMessageRevokedIntygKnowType(String certificateType) throws MessageNotWriteableException, IOException {
        String intyg = "intyg";
        String correlationId = "123";

        Message rawMessage = getIntygMessage(intyg, correlationId, certificateType, JmsReceiver.REVOKED);

        jmsReceiver.onMessage(rawMessage);

        verify(receiver, times(1))
            .accept(Mockito.eq(EventType.REVOKED), Mockito.eq(intyg), Mockito.eq(correlationId), Mockito.anyLong());
    }

    @Test
    void onMessageIntygSent() throws MessageNotWriteableException, IOException {
        String correlationId = "123";
        String recipient = "FK";

        Message rawMessage = getSentIntygMessage(correlationId, recipient);

        jmsReceiver.onMessage(rawMessage);

        verify(processIntygsentLog, times(1))
            .store(Mockito.eq(correlationId), Mockito.eq(recipient), Mockito.anyLong());
    }

    @Test
    void onMessageMessageSent() throws MessageNotWriteableException, IOException {
        String messageText = "message";
        String messageId = "m123";

        Message rawMessage = getSentMessage(messageText, messageId);

        jmsReceiver.onMessage(rawMessage);

        verify(processMessageLog, times(1))
            .store(Mockito.eq(MessageEventType.SENT), Mockito.eq(messageText), Mockito.eq(messageId), Mockito.anyLong());
    }

    private Message getSentMessage(String messageText, String messageId) throws MessageNotWriteableException, IOException {

        ActiveMQTextMessage message = new ActiveMQTextMessage();

        message.setText(messageText);
        message.setProperty(JmsReceiver.ACTION, JmsReceiver.ACTION_TYPE_MESSAGE_SENT);
        message.setProperty(JmsReceiver.MESSAGE_ID, messageId);

        return message;
    }

    private Message getSentIntygMessage(String correlationId, String recipient) throws MessageNotWriteableException, IOException {

        ActiveMQTextMessage message = new ActiveMQTextMessage();

        message.setText("");
        message.setProperty(JmsReceiver.ACTION, JmsReceiver.ACTION_TYPE_CERTIFICATE_SENT);
        message.setProperty(JmsReceiver.CERTIFICATE_SENT_RECIPIENT, recipient);
        message.setProperty(JmsReceiver.CERTIFICATE_ID, correlationId);

        return message;
    }

    private Message getIntygMessage(String intyg, String correlationId, String certificateType, String action)
        throws MessageNotWriteableException, IOException {

        ActiveMQTextMessage message = new ActiveMQTextMessage();

        message.setText(intyg);
        message.setProperty(JmsReceiver.ACTION, action);
        if (certificateType != null) {
            message.setProperty(JmsReceiver.CERTIFICATE_TYPE, certificateType);
        }
        message.setProperty(JmsReceiver.CERTIFICATE_ID, correlationId);

        return message;
    }
}