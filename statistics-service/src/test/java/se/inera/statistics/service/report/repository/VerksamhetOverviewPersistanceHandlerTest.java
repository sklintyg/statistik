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

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.Verksamhet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class VerksamhetOverviewPersistanceHandlerTest extends VerksamhetOverviewPersistenceHandler {

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

    @Before
    public void init() {
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Verksamhet.ENHET, Sex.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Verksamhet.ENHET, Sex.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g2", Verksamhet.ENHET, Sex.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g3", Verksamhet.ENHET, Sex.Female);
        diagnosgroupPersistenceHandler.count("id3", "2013-09", "g1", Verksamhet.ENHET, Sex.Male);
        diagnosgroupPersistenceHandler.count("id1", "2013-06", "g1", Verksamhet.ENHET, Sex.Male);

        casesPerMonth.count("id1", "2013-09", Verksamhet.ENHET, Sex.Female);
        casesPerMonth.count("id1", "2013-09", Verksamhet.ENHET, Sex.Female);
        casesPerMonth.count("id3", "2013-09", Verksamhet.ENHET, Sex.Male);
        casesPerMonth.count("id1", "2013-06", Verksamhet.ENHET, Sex.Male);

        aldersgruppPersistenceHandler.count("2013-09", "id1", "<21", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "<21", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "21-25", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "26-30", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id3", "<21", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Male);
        aldersgruppPersistenceHandler.count("2013-06", "id1", "<21", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Male);

        sjukskrivningsgrad.count("id1", "2013-09", "25", Verksamhet.ENHET, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-09", "100", Verksamhet.ENHET, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-09", "100", Verksamhet.ENHET, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-06", "25", Verksamhet.ENHET, Sex.Female);
        sjukskrivningsgrad.count("id1", "2013-06", "100", Verksamhet.ENHET, Sex.Female);

        sjukfallslangdGrupp.count("2013-09", "id1","<15 dagar", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        sjukfallslangdGrupp.count("2013-09", "id1","<15 dagar", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Male);
        sjukfallslangdGrupp.count("2013-09", "id3","<15 dagar", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Female);
        sjukfallslangdGrupp.count("2013-09", "id1",">365 dagar", RollingLength.QUARTER, Verksamhet.ENHET, Sex.Male);

    }

    @Test
    public void getOverviewTest() {
        LocalDate from = new LocalDate(2013, 7, 1);
        LocalDate to = new LocalDate(2013, 9, 1);
        Range range = new Range(from, to);

        VerksamhetOverviewResponse result = this.getOverview("id1", range);

        Assert.assertEquals(2, result.getDiagnosisGroups().get(0).getQuantity());
        Assert.assertEquals(100, result.getCasesPerMonthSexProportionPreviousPeriod().getFemaleProportion());
        Assert.assertEquals(0, result.getCasesPerMonthSexProportionPreviousPeriod().getMaleProportion());
        Assert.assertEquals(100, result.getCasesPerMonthSexProportionBeforePreviousPeriod().getMaleProportion());
        Assert.assertEquals(2, result.getTotalCases());
        Assert.assertEquals(100, result.getDiagnosisGroups().get(0).getAlternation());
        Assert.assertEquals(3, result.getAgeGroups().size());
        Assert.assertEquals(2, result.getAgeGroups().get(0).getQuantity());
        Assert.assertEquals(100, result.getAgeGroups().get(0).getAlternation());
        
        Assert.assertEquals(1, result.getDegreeOfSickLeaveGroups().get(0).getQuantity());
        Assert.assertEquals(2, result.getDegreeOfSickLeaveGroups().get(3).getQuantity());
        Assert.assertEquals(0, result.getDegreeOfSickLeaveGroups().get(0).getAlternation());
        Assert.assertEquals(100, result.getDegreeOfSickLeaveGroups().get(3).getAlternation());

        Assert.assertEquals(2, result.getSickLeaveLengthGroups().get(0).getQuantity());
        Assert.assertEquals(1, result.getSickLeaveLengthGroups().get(1).getQuantity());

        Assert.assertEquals(1, result.getLongSickLeavesTotal());
        Assert.assertEquals(0, result.getLongSickLeavesAlternation());
    }
}
