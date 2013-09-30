package se.inera.statistics.service.queue;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.processlog.Processor;
import se.inera.statistics.service.queue.Receiver;

@RunWith(MockitoJUnitRunner.class)
public class ReceiverTest {

    @Mock
    private ProcessLog processLog = Mockito.mock(ProcessLog.class);
    @Mock
    private Processor processor = Mockito.mock(Processor.class);

    @InjectMocks
    private Receiver receiver = new Receiver();

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void acceptedEventdataIsStored() {
        String data = JSONSource.readTemplateAsString().toString();

        receiver.accept(EventType.CREATED, data, "corr", 123L);

        verify(processLog).store(EventType.CREATED, data, "corr", 123L);
        verify(processor).accept(any(JsonNode.class), any(JsonNode.class));
    }
    // CHECKSTYLE:ON MagicNumber
}
