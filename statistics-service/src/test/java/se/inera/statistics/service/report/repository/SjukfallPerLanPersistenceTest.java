package se.inera.statistics.service.report.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.CasesPerCounty;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class SjukfallPerLanPersistenceTest extends SjukfallPerLanPersistenceHandler {


    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.parse("20131012", ISODateTimeFormat.basicDate()).getMillis());
        DateTimeUtils.setCurrentMillisFixed(DateTime.parse("20131012", ISODateTimeFormat.basicDate()).getMillis());
        count("2013-09", "id1", "14", RollingLength.SINGLE_MONTH, Sex.Female);
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void resultIsKeptWhenNotLessThanCutoff() {
        ReflectionTestUtils.setField(this, "cutoff", 0);
        final Range range = new Range(1);
        SimpleDualSexResponse<SimpleDualSexDataRow> statistics = this.getStatistics(range);
        assertEquals(1, statistics.getRows().get(18).getData().getFemale());
    }

    @Test
    public void resultIsZeroedWhenLessThanCutoff() {
        final Range range = new Range(1);
        SimpleDualSexResponse<SimpleDualSexDataRow> statistics = this.getStatistics(range);
        assertEquals(0, statistics.getRows().get(18).getData().getFemale());
    }
}
