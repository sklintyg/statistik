package se.inera.statistics.service.processlog;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSADecorator;

import com.fasterxml.jackson.databind.JsonNode;

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
        IntygEvent event = new IntygEvent(EventType.CREATED, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        when(hsa.decorate(any(JsonNode.class), anyString())).thenReturn(JSONParser.parse("{}"));
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
        verify(processor).accept(any(JsonNode.class), any(JsonNode.class), Mockito.anyLong());
    }

    @Test
    public void failingHsaSkipsProcessing() {
        IntygEvent event = new IntygEvent(EventType.CREATED, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        when(hsa.decorate(any(JsonNode.class), anyString())).thenReturn(null);
        int count = consumer.processBatch();
        assertEquals(0, count);
        verify(processLog).getPending(100);
        verify(processor, Mockito.never()).accept(any(JsonNode.class), any(JsonNode.class), Mockito.anyLong());
    }

    @Test
    public void failingAcceptContinuesProcessing() {
        IntygEvent event = new IntygEvent(EventType.CREATED, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        when(hsa.decorate(any(JsonNode.class), anyString())).thenReturn(JSONParser.parse("{}"));
        doThrow(new IllegalArgumentException("Invalid intyg")).when(processor).accept(any(JsonNode.class), any(JsonNode.class), Mockito.anyLong());
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
    }

    @Test
    public void deleteEventsAreSkipped() {
        IntygEvent event = new IntygEvent(EventType.REVOKED, "{}", "correlationId", 1);
        when(processLog.getPending(100)).thenReturn(Collections.singletonList(event));
        int count = consumer.processBatch();
        assertEquals(1, count);
        verify(processLog).getPending(100);
        verify(processor, Mockito.never()).accept(any(JsonNode.class), any(JsonNode.class), Mockito.anyLong());
        verify(hsa, Mockito.never()).decorate(any(JsonNode.class), anyString());
    }
    // CHECKSTYLE:ON MagicNumber
}
