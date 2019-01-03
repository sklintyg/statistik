/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.ifv.statistics.spi.authorization.impl.HsaCommunicationException;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.hsa.HsaInfo;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogConsumerImplTest {

    @Mock
    private ProcessLog processLog = mock(ProcessLog.class);

    @Mock
    private Processor processor = mock(Processor.class);

    @Mock
    private HSADecorator hsa = mock(HSADecorator.class);

    @InjectMocks
    private LogConsumerImpl consumer = new LogConsumerImpl();

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
        verify(hsa, Mockito.never()).getHSAInfo(Mockito.anyString());
        verify(processLog, Mockito.never()).confirm(Mockito.anyLong());
    }

    @Test
    public void processingSucceedsForOneEvent() {
        String data = JSONSource.readTemplateAsString();
        IntygEvent event = new IntygEvent(EventType.CREATED, data, "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        when(hsa.decorate(any(JsonNode.class), anyString())).thenReturn(new HsaInfo(null, null, null, null));
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
        verify(processor).accept(any(IntygDTO.class), any(HsaInfo.class), Mockito.anyLong(), anyString(), any(EventType.class));
    }

    @Test
    public void failingHsaSkipsProcessing() {
        IntygEvent event = new IntygEvent(EventType.CREATED, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        when(hsa.decorate(any(JsonNode.class), anyString())).thenThrow(new HsaCommunicationException("", null));
        int count = consumer.processBatch();
        assertEquals(0, count);
        verify(processLog).getPending(100);
        verify(processor, Mockito.never()).accept(any(IntygDTO.class), any(HsaInfo.class), Mockito.anyLong(), anyString(), any(EventType.class));
    }

    @Test
    public void failingAcceptContinuesProcessing() {
        IntygEvent event = new IntygEvent(EventType.CREATED, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        when(hsa.decorate(any(JsonNode.class), anyString())).thenReturn(new HsaInfo(null, null, null, null));
        doThrow(new IllegalArgumentException("Invalid intyg")).when(processor).accept(any(IntygDTO.class), any(HsaInfo.class), Mockito.anyLong(), anyString(), any(EventType.class));
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
    }

    @Test
    public void deleteEventsAreAdded() {
        IntygEvent event = new IntygEvent(EventType.REVOKED, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
    }
    // CHECKSTYLE:ON MagicNumber
}
