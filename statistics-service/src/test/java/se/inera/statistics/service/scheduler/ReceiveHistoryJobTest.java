package se.inera.statistics.service.scheduler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.processlog.Receiver;

@RunWith(MockitoJUnitRunner.class)
public class ReceiveHistoryJobTest {

    @Mock
    private Receiver receiver = Mockito.mock(Receiver.class);
    
    @InjectMocks
    ReceiveHistoryJob history = new ReceiveHistoryJob();

    @Test
    public void initialRateIsZero() {
        assertEquals(0,  history.getCurrentRate());
    }

    @Test
    public void rateIncreasesWithMeasurement() {
        Mockito.when(receiver.getAccepted()).thenReturn(1L);
        history.checkReceived();
        assertEquals(1, history.getCurrentRate());
    }

    @Test
    public void rateFlattensForConstantValues() {
        Mockito.when(receiver.getAccepted()).thenReturn(1L);
        for (int i = 0; i < ReceiveHistoryJob.HISTORY_ITEMS - 1; i++) {
            history.checkReceived();
        }
        assertEquals(1, history.getCurrentRate());
        history.checkReceived();
        assertEquals(0, history.getCurrentRate());
    }

    @Test
    public void rollOverHistory() {
        // Fill history with 1
        Mockito.when(receiver.getAccepted()).thenReturn(1L);
        for (int i = 0; i < ReceiveHistoryJob.HISTORY_ITEMS; i++) {
            history.checkReceived();
        }
        assertEquals(0, history.getCurrentRate());

        // Add 3 to history
        Mockito.when(receiver.getAccepted()).thenReturn(3L);
        history.checkReceived();
        assertEquals(2, history.getCurrentRate());

        // Fill history with 3
        for (int i = 0; i < ReceiveHistoryJob.HISTORY_ITEMS; i++) {
            history.checkReceived();
        }
        assertEquals(0, history.getCurrentRate());
    }
}
