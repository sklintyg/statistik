package se.inera.statistics.service.report.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.model.Range;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class SjukfallPerLanPersistenceTest extends SjukfallPerLanPersistenceHandler {

    @Test
    public void getIt() {
        final Range range = new Range(12);
        this.getStatistics(range);
    }
}
