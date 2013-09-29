package se.inera.statistics.service.queue;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.queue.Receiver;

public class ReceiverTest {

    private ProcessLog processLog = Mockito.mock(ProcessLog.class);

    private Receiver receiver = new Receiver();

    @Before
    public void setup() {
        receiver.setProcessLog(processLog);
    }

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void acceptedEventdataIsStored() {
        String data = "data";
        receiver.accept(EventType.CREATED, data, "corr", 123L);
        verify(processLog).store(EventType.CREATED, "data", "corr", 123L);
    }
    // CHECKSTYLE:ON MagicNumber
}
