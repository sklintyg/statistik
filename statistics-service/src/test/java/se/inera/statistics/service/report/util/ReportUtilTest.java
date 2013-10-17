package se.inera.statistics.service.report.util;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.Range;

public class ReportUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(ReportUtilTest.class);

    @Test
    public void testRangeWithinYear() {
        Range testRange = new Range(new LocalDate(2013, 5, 1), new LocalDate(2013, 6, 1));

        Range result = ReportUtil.getPreviousPeriod(testRange);

        LOG.info(result.getFrom() + " -- " + result.getTo());
        Assert.assertEquals(2013, result.getFrom().getYear());
        Assert.assertEquals(3, result.getFrom().getMonthOfYear());
        Assert.assertEquals(2013, result.getTo().getYear());
        Assert.assertEquals(4, result.getTo().getMonthOfYear());
    }

    @Test
    public void testRangeBiggerThanOneYear() {
        Range testRange = new Range(new LocalDate(2012, 1, 1), new LocalDate(2013, 6, 1));

        Range result = ReportUtil.getPreviousPeriod(testRange);

        LOG.info(result.getFrom() + " -- " + result.getTo());
        Assert.assertEquals(2010, result.getFrom().getYear());
        Assert.assertEquals(7, result.getFrom().getMonthOfYear());
        Assert.assertEquals(2011, result.getTo().getYear());
        Assert.assertEquals(12, result.getTo().getMonthOfYear());
    }
}
