package se.inera.statistics.service.sjukfall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
public class SjukfallServiceTest extends SjukfallService {

    DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendYear(4,4).appendLiteral('-').appendMonthOfYear(2).appendLiteral('-').appendDayOfMonth(2).toFormatter();

    @Test
    public void registeringPeriodReturnsId() {
        SjukfallInfo id = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-25"));
        assertNotNull(id);
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
        SjukfallInfo id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        int expired = expire(date("2013-02-05"));
        SjukfallInfo id2 = register("personnummer", "vardgivare", date("2013-02-10"), date("2013-02-25"));
        assertEquals(1, expired);
        assertFalse(id1.equals(id2));
    }

    @Test
    public void expireExpiresOnlyOld() {
        SjukfallInfo shouldExpire = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        SjukfallInfo active = register("personnummer", "vardgivare2", date("2013-01-01"), date("2013-02-01"));

        int expired = expire(date("2013-02-05"));

        SjukfallInfo newSjukfall = register("personnummer", "vardgivare", date("2013-02-10"), date("2013-02-25"));
        SjukfallInfo stillActive = register("personnummer", "vardgivare2", date("2013-02-10"), date("2013-02-25"));

        assertEquals(1, expired);
        assertFalse(shouldExpire.equals(newSjukfall));
        assertEquals(active, stillActive);
    }

    private LocalDate date(String stringDate) {
        return formatter.parseLocalDate(stringDate);
    }
}
