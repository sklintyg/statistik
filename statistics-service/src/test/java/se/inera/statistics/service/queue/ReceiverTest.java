package se.inera.statistics.service.queue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.OrderedProcess;
import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.processlog.Processor;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class ReceiverTest {

    @Mock
    private ProcessLog processLog = Mockito.mock(ProcessLog.class);
    @Mock
    private Processor processor = Mockito.mock(Processor.class);
    @Mock
    private HSADecorator hsaDecorator = Mockito.mock(HSADecorator.class);
    @Mock
    private OrderedProcess orderedProcess = Mockito.mock(OrderedProcess.class);

    @InjectMocks
    private Receiver receiver = new Receiver();

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void acceptedEventdataIsStored() {
        String data = JSONSource.readTemplateAsString().toString();

        receiver.accept(EventType.CREATED, data, "corr", 123L);

        verify(processLog).store(EventType.CREATED, data, "corr", 123L);
        verify(hsaDecorator).decorate(any(JsonNode.class), anyString());
        verify(orderedProcess).register(any(JsonNode.class), anyString());
    }
    // CHECKSTYLE:ON MagicNumber
}
