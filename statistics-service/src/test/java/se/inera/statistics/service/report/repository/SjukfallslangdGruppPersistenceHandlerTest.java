package se.inera.statistics.service.report.repository;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class SjukfallslangdGruppPersistenceHandlerTest extends SjukfallslangdGruppPersistenceHandler {

    @Test
    public void testGetHistoricalStatistics() {
        count("2012-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        count("2012-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        count("2012-02", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        count("2011-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        count("2012-01", "verksamhet2", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        count("2012-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);

        SickLeaveLengthResponse result = this.getHistoricalStatistics("verksamhet1", new LocalDate("2012-01-01"), RollingLength.QUARTER);

        Assert.assertEquals(1, result.getRows().size());
        Assert.assertEquals(2, result.getRows().get(0).getFemale());
    }

    @Test
    public void testGetCurrentStatistics() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate nextMonth = now.plusMonths(1);
        LocalDate prevYear = now.minusYears(1);
        String period1 = now.toString("yyyy-MM");
        String period2 = nextMonth.toString("yyyy-MM");
        String period3 = prevYear.toString("yyyy-MM");
        count(period1, "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count(period1, "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Male);
        count(period2, "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count(period3, "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count(period1, "verksamhet2", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count(period1, "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(1).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);

        SickLeaveLengthResponse result = this.getCurrentStatistics("verksamhet1");

        Assert.assertEquals(1, result.getRows().size());
        Assert.assertEquals(2, result.getRows().get(0).getFemale());
        Assert.assertEquals(1, result.getRows().get(0).getMale());
    }

    @Test
    public void testGetLongSickLeaves() throws Exception {
        // CHECKSTYLE:OFF MagicNumber
        count("2013-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(90).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count("2013-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(91).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count("2013-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(370).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Male);
        count("2013-02", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(91).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count("2012-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(91).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count("2013-01", "verksamhet2", SjukfallslangdUtil.RANGES.rangeFor(91).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);
        count("2013-01", "verksamhet1", SjukfallslangdUtil.RANGES.rangeFor(91).getName(), RollingLength.SINGLE_MONTH, Verksamhet.ENHET, Sex.Female);

        SimpleDualSexResponse<SimpleDualSexDataRow> result = getLongSickLeaves("verksamhet1", new Range(new LocalDate("2012-04-01"), new LocalDate("2013-09-01")));

        Assert.assertEquals(18, result.getRows().size());
        Assert.assertEquals(2, (int) result.getRows().get(9).getFemale());
        Assert.assertEquals(1, (int) result.getRows().get(9).getMale());
        // CHECKSTYLE:ON MagicNumber
    }

}
