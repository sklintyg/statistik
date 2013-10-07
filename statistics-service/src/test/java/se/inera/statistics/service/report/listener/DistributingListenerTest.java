package se.inera.statistics.service.report.listener;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DistributingListenerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noPrevousEndReturnsNewStartMonth() {
        LocalDate startMonth = new LocalDate("2013-08-05");
        LocalDate firstDateMonth = DistributingListener.getFirstDateMonth(null, startMonth);
        assertEquals(startMonth.withDayOfMonth(1), firstDateMonth);
    }

    @Test
    public void prevousEndBeforeNewMonthReturnsNewStartMonth() {
        LocalDate previousEnd = new LocalDate("2013-07-21");
        LocalDate startMonth = new LocalDate("2013-08-05");
        LocalDate firstDateMonth = DistributingListener.getFirstDateMonth(previousEnd, startMonth);
        assertEquals(startMonth.withDayOfMonth(1), firstDateMonth);
    }

    @Test
    public void prevousEndSameAsNewMonthReturnsNextMonth() {
        LocalDate previousEnd = new LocalDate("2013-08-02");
        LocalDate startMonth = new LocalDate("2013-08-05");
        LocalDate firstDateMonth = DistributingListener.getFirstDateMonth(previousEnd, startMonth);
        assertEquals(startMonth.withDayOfMonth(1).plusMonths(1), firstDateMonth);
    }
}
