/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.helper.SendMessageToCareHelper;
import se.inera.statistics.service.processlog.Processor;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageLogConsumerImplTest {

    @Mock
    private ProcessMessageLog processLog;
    @Mock
    private SendMessageToCareHelper sendMessageToCareHelper;
    @Mock
    private Processor processor;
    @InjectMocks
    private MessageLogConsumerImpl consumer;

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void batchSizeIsUsed() {
        consumer.processBatch();
        verify(processLog).getPending(100);
    }

    @Test
    public void forNoPendingNoJobIsDoneAnd0IsReturned() {
        int count = consumer.processBatch();
        assertEquals(0, count);
        verify(processLog, Mockito.never()).confirm(Mockito.anyLong());
    }


    @Test
    public void processingSucceedsForOneEvent() {
        MessageEvent event = new MessageEvent(MessageEventType.SENT, "<xml>", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
        verify(processor).accept(any(SendMessageToCareType.class), Mockito.anyLong(), anyString(), any(MessageEventType.class));
    }

    @Test
    public void failingAcceptContinuesProcessing() {
        MessageEvent event = new MessageEvent(MessageEventType.SENT, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        doThrow(new IllegalArgumentException("Invalid meddelande")).when(processor).accept(any(SendMessageToCareType.class), Mockito.anyLong(), anyString(), any(MessageEventType.class));
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
    }
    // CHECKSTYLE:ON MagicNumber
}
