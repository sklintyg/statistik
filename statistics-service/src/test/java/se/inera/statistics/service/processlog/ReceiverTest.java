package se.inera.statistics.service.processlog;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ReceiverTest {
    
    private ProcessLog processLog = Mockito.mock(ProcessLog.class);
    
    private Receiver receiver = new Receiver();

    @Before
    public void setup() {
        receiver.setProcessLog(processLog);
    }
    
    @Test
    public void acceptedEventdataIsStored() {
        String data = "data";
        receiver.accept(EventType.CREATED, data);
        verify(processLog).store(EventType.CREATED, "data");
    }

}
