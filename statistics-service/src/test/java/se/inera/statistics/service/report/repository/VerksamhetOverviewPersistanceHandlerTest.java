package se.inera.statistics.service.report.repository;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class VerksamhetOverviewPersistanceHandlerTest extends VerksamhetOverviewPersistenceHandler {

    @Autowired
    private DiagnosisGroups diagnosgroupPersistenceHandler;

    @Before
    public void init() {
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Sex.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Sex.Female);
        diagnosgroupPersistenceHandler.count("id3", "2013-09", "g1", Sex.Male);
        diagnosgroupPersistenceHandler.count("id1", "2012-09", "g1", Sex.Male);
    }

    @Test
    public void getDiagnosisGroupsTest() {
        LocalDate from = new LocalDate(2013,8,1);
        LocalDate to = new LocalDate(2013,10,1);
        Range range = new Range(from, to);

        VerksamhetOverviewResponse result = this.getOverview("id1", range);

        Assert.assertEquals(2, result.getDiagnosisGroups().get(0).getQuantity());
    }
}
