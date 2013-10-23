package se.inera.statistics.service.report.repository;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.*;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.Verksamhet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class OverviewPersistanceHandlerTest extends OverviewPersistenceHandler {

    @Autowired
    private DiagnosisGroups diagnosgroupPersistenceHandler;

    @Autowired
    private CasesPerMonth casesPerMonth;

    @Autowired
    private AgeGroups aldersgruppPersistenceHandler;

    @Autowired
    private DegreeOfSickLeave sjukskrivningsgrad;

    @Autowired
    private SjukfallslangdGrupp sjukfallslangdGrupp;

    @Autowired
    private NationellUpdater nationellUpdater;

    @Before
    public void init() {

        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Verksamhet.VARDGIVARE, Sex.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Verksamhet.VARDGIVARE, Sex.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g2", Verksamhet.VARDGIVARE, Sex.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g3", Verksamhet.VARDGIVARE, Sex.Female);
        diagnosgroupPersistenceHandler.count("id3", "2013-09", "g1", Verksamhet.VARDGIVARE, Sex.Male);
        diagnosgroupPersistenceHandler.count("id1", "2013-06", "g1", Verksamhet.VARDGIVARE, Sex.Male);

        casesPerMonth.count("id1", "2013-09", Verksamhet.VARDGIVARE, Sex.Female);
        casesPerMonth.count("id1", "2013-09", Verksamhet.VARDGIVARE, Sex.Female);
        casesPerMonth.count("id3", "2013-09", Verksamhet.VARDGIVARE, Sex.Male);
        casesPerMonth.count("id1", "2013-06", Verksamhet.VARDGIVARE, Sex.Male);

        aldersgruppPersistenceHandler.count("2013-09", "id1", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "21-25", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "26-30", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id3", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Male);
        aldersgruppPersistenceHandler.count("2013-06", "id1", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Male);

        sjukskrivningsgrad.count("id1", "2013-09", 25, Verksamhet.VARDGIVARE, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-09", 100, Verksamhet.VARDGIVARE, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-09", 100, Verksamhet.VARDGIVARE, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-06", 25, Verksamhet.VARDGIVARE, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-06", 100, Verksamhet.VARDGIVARE, Sex.Female);

        sjukfallslangdGrupp.count("2013-09", "id1","<15 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Female);
        sjukfallslangdGrupp.count("2013-09", "id1","<15 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Male);
        sjukfallslangdGrupp.count("2013-09", "id3","<15 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Female);
        sjukfallslangdGrupp.count("2013-09", "id1",">365 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Sex.Male);

        nationellUpdater.updateCasesPerMonth();
        nationellUpdater.updateAldersgrupp();
        nationellUpdater.updateDiagnosgrupp();
        nationellUpdater.updateDiagnosundergrupp();
        nationellUpdater.updateSjukfallslangd();
        nationellUpdater.updateSjukskrivningsgrad();
    }

    @Test
    public void getOverviewTest() {
        LocalDate from = new LocalDate(2013, 7, 1);
        LocalDate to = new LocalDate(2013, 9, 1);
        Range range = new Range(from, to);

        OverviewResponse result = this.getOverview(range);

        Assert.assertEquals(67, result.getCasesPerMonthSexProportion().getFemale());
        Assert.assertEquals(33, result.getCasesPerMonthSexProportion().getMale());
        Assert.assertEquals(3, result.getDiagnosisGroups().get(0).getQuantity());
        Assert.assertEquals(200, result.getDiagnosisGroups().get(0).getAlternation());
        Assert.assertEquals(3, result.getAgeGroups().size());
        Assert.assertEquals(3, result.getAgeGroups().get(0).getQuantity());
        Assert.assertEquals(200, result.getAgeGroups().get(0).getAlternation());

        Assert.assertEquals(1, result.getDegreeOfSickLeaveGroups().get(0).getQuantity());
        Assert.assertEquals(2, result.getDegreeOfSickLeaveGroups().get(3).getQuantity());
        Assert.assertEquals(0, result.getDegreeOfSickLeaveGroups().get(0).getAlternation());
        Assert.assertEquals(100, result.getDegreeOfSickLeaveGroups().get(3).getAlternation());

        Assert.assertEquals(3, result.getSickLeaveLengthGroups().get(0).getQuantity());
        Assert.assertEquals(1, result.getSickLeaveLengthGroups().get(1).getQuantity());

        Assert.assertEquals(1, result.getLongSickLeavesTotal());
        Assert.assertEquals(0, result.getLongSickLeavesAlternation());
    }
}
