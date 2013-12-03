package se.inera.statistics.service.sjukfall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class SjukfallServiceTest extends SjukfallService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Test
    public void registeringPeriodReturnsSjukfallsInfo() {
        SjukfallInfo info = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-25"));
        assertNotNull(info);
        assertEquals(25, info.getLangd());
    }

    @Test
    public void registeringSamePeriodDifferentUsersReturnsDifferentId() {
        SjukfallInfo id1 = register("personnummer1", "vardgivare", date("2013-01-01"), date("2013-01-25"));
        SjukfallInfo id2 = register("personnummer2", "vardgivare", date("2013-01-01"), date("2013-01-25"));
        assertFalse(id1.equals(id2));
    }

    @Test
    public void registeringSamePeriodDifferentVardgivareReturnsDifferentId() {
        SjukfallInfo id1 = register("personnummer", "vardgivare1", date("2013-01-01"), date("2013-01-25"));
        SjukfallInfo id2 = register("personnummer", "vardgivare2", date("2013-01-01"), date("2013-01-25"));
        assertFalse(id1.equals(id2));
    }

    @Test
    public void registeringOverlappingPeriodsReturnsSameId() {
        SjukfallInfo id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-01-25"), date("2013-02-15"));
        assertEquals(id1, id2);
    }

    @Test
    public void registeringClosePeriodsReturnsSameId() {
        SjukfallInfo id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-01-25"), date("2013-02-15"));
        assertEquals(id1, id2);
    }

    @Test
    public void registeringNotClosePeriodsReturnsDifferentId() {
        register("personnummer", "vardgivare", date("2012-01-01"), date("2012-01-28"));
        register("personnummer", "vardgivare", date("2012-11-01"), date("2013-01-28"));
        SjukfallInfo id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-02-10"), date("2013-02-25"));
        assertFalse(id1.equals(id2));
    }

    @Test
    public void expireExpiresOnlyOld() {
        SjukfallInfo shouldExpire = register("personnummer", "vardgivare", date("2012-01-01"), date("2012-01-28"));
        SjukfallInfo active = register("personnummer", "vardgivare2", date("2013-01-01"), date("2013-02-06"));

        SjukfallInfo newSjukfall = register("personnummer", "vardgivare", date("2013-02-10"), date("2013-02-25"));
        SjukfallInfo stillActive = register("personnummer", "vardgivare2", date("2013-02-10"), date("2013-02-25"));

        assertFalse(shouldExpire.equals(newSjukfall));
        assertEquals(active, stillActive);
    }

    @Test
    public void keepOriginalStartDate() {
        SjukfallInfo id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-01-25"), date("2013-02-15"));
        assertEquals(date("2013-01-01"), id1.getStart());
        assertEquals(date("2013-01-28"), id1.getEnd());
        assertEquals(date("2013-01-01"), id2.getStart());
        assertEquals(date("2013-02-15"), id2.getEnd());
    }

    @Test
    public void overlappingKeepsPreviousEndDate() {
        register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-01-25"), date("2013-02-15"));
        assertEquals(date("2013-01-28"), id2.getPrevEnd());
    }

    private LocalDate date(String stringDate) {
        return FORMATTER.parseLocalDate(stringDate);
    }

    @Test
    public void consecutiveIntygAndThenIntygWithFiveDayGap() {
        SjukfallInfo id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-15"));
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-01-15"), date("2013-01-16"));
        SjukfallInfo id3 = register("personnummer", "vardgivare", date("2013-01-21"), date("2013-01-22"));
        assertEquals(date("2013-01-01"), id1.getStart());
        assertEquals(date("2013-01-15"), id1.getEnd());
        assertEquals(date("2013-01-01"), id2.getStart());
        assertEquals(date("2013-01-16"), id2.getEnd());
        assertEquals(date("2013-01-01"), id3.getStart());
        assertEquals(date("2013-01-22"), id3.getEnd());
    }


    @Test
    public void consecutiveIntygWithFiveDayGap() {
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-16"));
        SjukfallInfo id3 = register("personnummer", "vardgivare", date("2013-01-21"), date("2013-01-22"));
        assertEquals(date("2013-01-01"), id2.getStart());
        assertEquals(date("2013-01-16"), id2.getEnd());
        assertEquals(date("2013-01-01"), id3.getStart());
        assertEquals(date("2013-01-22"), id3.getEnd());
    }

    @Test
    public void consecutiveIntygWithSixDayGap() {
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-16"));
        SjukfallInfo id3 = register("personnummer", "vardgivare", date("2013-01-22"), date("2013-01-23"));
        assertEquals(date("2013-01-01"), id2.getStart());
        assertEquals(date("2013-01-16"), id2.getEnd());
        assertEquals(date("2013-01-22"), id3.getStart());
        assertEquals(date("2013-01-23"), id3.getEnd());
    }



}
