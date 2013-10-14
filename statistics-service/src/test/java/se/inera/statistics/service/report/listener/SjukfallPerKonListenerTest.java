package se.inera.statistics.service.report.listener;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import se.inera.statistics.service.report.model.Sex;

public class SjukfallPerKonListenerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SjukfallPerKonListener listener = Mockito.spy(new SjukfallPerKonListener());

    @Test
    public void acceptWithNoPreviousStartsCallingAtStartMonth() {
        ArgumentCaptor<LocalDate> capture = ArgumentCaptor.forClass(LocalDate.class);
        doNothing().when(listener).accept(anyString(), capture.capture(), any(Sex.class));

        LocalDate firstDate = new LocalDate("2013-01-01");
        LocalDate lastDate = new LocalDate("2013-01-01");
        listener.acceptPeriod("hsaId", firstDate, lastDate, Sex.Female);

        assertEquals(firstDate, capture.getValue());
    }

    @Test
    public void acceptWithNoPreviousCallsFromStartMonthToEndMonth() {
        ArgumentCaptor<LocalDate> capture = ArgumentCaptor.forClass(LocalDate.class);
        doNothing().when(listener).accept(anyString(), capture.capture(), any(Sex.class));

        LocalDate firstDate = new LocalDate("2013-01-01");
        LocalDate lastDate = new LocalDate("2013-03-01");
        listener.acceptPeriod("hsaId", firstDate, lastDate, Sex.Female);

        Iterator<LocalDate> allValues = capture.getAllValues().iterator();
        assertEquals(firstDate, allValues.next());
        assertEquals(firstDate.plusMonths(1), allValues.next());
        assertEquals(firstDate.plusMonths(2), allValues.next());
        
        thrown.expect(NoSuchElementException.class);
        allValues.next();
    }
}
