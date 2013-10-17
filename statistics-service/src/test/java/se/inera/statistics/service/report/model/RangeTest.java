package se.inera.statistics.service.report.model;

import static org.junit.Assert.*;

import org.joda.time.LocalDate;
import org.junit.Test;

public class RangeTest {

    @Test
    public void testToStringWithToAndFromOnSameYear() {
        Range range = new Range(new LocalDate(2013, 10, 01), new LocalDate(2013, 12, 01));
        assertEquals("okt-dec 2013", range.toString());
    }

    @Test
    public void testToStringWithToAndFromOnDifferentYears() {
        Range range = new Range(new LocalDate(2013, 10, 01), new LocalDate(2014, 12, 01));
        assertEquals("okt 2013-dec 2014", range.toString());
    }
    
}
