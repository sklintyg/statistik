package se.inera.statistics.service.report.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import se.inera.statistics.service.demo.UtlatandeBuilder;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

public class AbstractListenerTest {
    
    private AbstractListener<String> listener = spy(new AbstractListener<String>() {
        String setup(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, LocalDate start, LocalDate end) {
            return "Context";
        }

        boolean accept(String token, String period) {
            return false;
        }
    });

    @Test
    public void noPrevousEndReturnsNewStartMonth() {
        LocalDate startMonth = new LocalDate("2013-08-05");
        LocalDate firstDateMonth = AbstractListener.getFirstDateMonth(null, startMonth);
        assertEquals(startMonth.withDayOfMonth(1), firstDateMonth);
    }

    @Test
    public void prevousEndBeforeNewMonthReturnsNewStartMonth() {
        LocalDate previousEnd = new LocalDate("2013-07-21");
        LocalDate startMonth = new LocalDate("2013-08-05");
        LocalDate firstDateMonth = AbstractListener.getFirstDateMonth(previousEnd, startMonth);
        assertEquals(startMonth.withDayOfMonth(1), firstDateMonth);
    }

    @Test
    public void prevousEndSameAsNewMonthReturnsNextMonth() {
        LocalDate previousEnd = new LocalDate("2013-08-02");
        LocalDate startMonth = new LocalDate("2013-08-05");
        LocalDate firstDateMonth = AbstractListener.getFirstDateMonth(previousEnd, startMonth);
        assertEquals(startMonth.withDayOfMonth(1).plusMonths(1), firstDateMonth);
    }

    @Test
    public void eachMonthIsCalledFormatted() {
        UtlatandeBuilder builder = new UtlatandeBuilder();

        JsonNode utlatande = builder.build("19121212-1212", new LocalDate("2013-08-02"), new LocalDate("2013-09-05"), "vardenhet", "A00", 50);
        SjukfallInfo sjukfall = mock(SjukfallInfo.class);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(listener.accept(eq("Context"), captor.capture())).thenReturn(false);

        listener.accept(sjukfall, utlatande, null);

        assertEquals(Arrays.asList("2013-08", "2013-09"), captor.getAllValues());
    }
}
