package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.*;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Verksamhet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class OverviewPersistanceHandlerTest extends OverviewPersistenceHandler {

    @Autowired
    private Diagnosgrupp diagnosgroupPersistenceHandler;

    @Autowired
    private SjukfallPerManad sjukfallPerManad;

    @Autowired
    private Aldersgrupp aldersgruppPersistenceHandler;

    @Autowired
    private Sjukskrivningsgrad sjukskrivningsgrad;

    @Autowired
    private SjukfallslangdGrupp sjukfallslangdGrupp;

    @Autowired
    private NationellUpdater nationellUpdater;

    @Autowired
    private SjukfallPerLan sjukfallPerLan;

    @Before
    public void init() {

        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Verksamhet.VARDGIVARE, Kon.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g1", Verksamhet.VARDGIVARE, Kon.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g2", Verksamhet.VARDGIVARE, Kon.Female);
        diagnosgroupPersistenceHandler.count("id1", "2013-09", "g3", Verksamhet.VARDGIVARE, Kon.Female);
        diagnosgroupPersistenceHandler.count("id3", "2013-09", "g1", Verksamhet.VARDGIVARE, Kon.Male);
        diagnosgroupPersistenceHandler.count("id1", "2013-06", "g1", Verksamhet.VARDGIVARE, Kon.Male);

        sjukfallPerManad.count("id1", "2013-09", Verksamhet.VARDGIVARE, Kon.Female);
        sjukfallPerManad.count("id1", "2013-09", Verksamhet.VARDGIVARE, Kon.Female);
        sjukfallPerManad.count("id3", "2013-09", Verksamhet.VARDGIVARE, Kon.Male);
        sjukfallPerManad.count("id1", "2013-06", Verksamhet.VARDGIVARE, Kon.Male);

        aldersgruppPersistenceHandler.count("2013-09", "id1", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "21-25", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id1", "26-30", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Female);
        aldersgruppPersistenceHandler.count("2013-09", "id3", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Male);
        aldersgruppPersistenceHandler.count("2013-06", "id1", "<21", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Male);

        sjukskrivningsgrad.count("id1", "2013-09", "25", Verksamhet.VARDGIVARE, Kon.Female);
        sjukskrivningsgrad.count("id1", "2013-09", "100", Verksamhet.VARDGIVARE, Kon.Female);
        sjukskrivningsgrad.count("id1", "2013-09", "100", Verksamhet.VARDGIVARE, Kon.Female);
        sjukskrivningsgrad.count("id1", "2013-06", "25", Verksamhet.VARDGIVARE, Kon.Female);
        sjukskrivningsgrad.count("id1", "2013-06", "100", Verksamhet.VARDGIVARE, Kon.Female);

        sjukfallslangdGrupp.count("2013-09", "id1", "<15 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Female);
        sjukfallslangdGrupp.count("2013-09", "id1", "<15 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Male);
        sjukfallslangdGrupp.count("2013-09", "id3", "<15 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Female);
        sjukfallslangdGrupp.count("2013-09", "id1", ">365 dagar", RollingLength.QUARTER, Verksamhet.VARDGIVARE, Kon.Male);

        sjukfallPerLan.count("2013-09", "id1", "14", RollingLength.QUARTER, Kon.Female);
        sjukfallPerLan.count("2013-09", "id1", "14", RollingLength.QUARTER, Kon.Male);
        sjukfallPerLan.count("2013-09", "id2", "01", RollingLength.QUARTER, Kon.Female);
        sjukfallPerLan.count("2013-06", "id1", "14", RollingLength.QUARTER, Kon.Female);

    }

    private void updateNational(int cutoff) {
        ReflectionTestUtils.invokeSetterMethod(nationellUpdater, "cutoff", cutoff);
        nationellUpdater.updateCasesPerMonth();
        nationellUpdater.updateAldersgrupp();
        nationellUpdater.updateDiagnosgrupp();
        nationellUpdater.updateDiagnosundergrupp();
        nationellUpdater.updateSjukfallslangd();
        nationellUpdater.updateSjukskrivningsgrad();
    }

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void getOverviewTest() {
        updateNational(0);
        final LocalDate from = new LocalDate(2013, 7, 1);
        final LocalDate to = new LocalDate(2013, 9, 1);
        Range range = new Range(from, to);

        OverviewResponse result = this.getOverview(range);

        Assert.assertEquals(67, result.getCasesPerMonthSexProportion().getFemaleProportion());
        Assert.assertEquals(33, result.getCasesPerMonthSexProportion().getMaleProportion());
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

        Assert.assertEquals(1, result.getPerCounty().get(0).getQuantity());
        Assert.assertEquals(0, result.getPerCounty().get(0).getAlternation());
        Assert.assertEquals(2, result.getPerCounty().get(1).getQuantity());
        Assert.assertEquals(100, result.getPerCounty().get(1).getAlternation());
    }

    @Test
    public void getOverviewLowCutoffTest() {
        updateNational(2);
        final LocalDate from = new LocalDate(2013, 7, 1);
        final LocalDate to = new LocalDate(2013, 9, 1);
        Range range = new Range(from, to);

        OverviewResponse result = this.getOverview(range);

        Assert.assertEquals(100, result.getCasesPerMonthSexProportion().getFemaleProportion());
        Assert.assertEquals(0, result.getCasesPerMonthSexProportion().getMaleProportion());
        Assert.assertEquals(2, result.getDiagnosisGroups().get(0).getQuantity());
        Assert.assertEquals(0, result.getDiagnosisGroups().get(0).getAlternation());
        Assert.assertEquals(3, result.getAgeGroups().size());
        Assert.assertEquals(2, result.getAgeGroups().get(0).getQuantity());
        Assert.assertEquals(0, result.getAgeGroups().get(0).getAlternation());

        Assert.assertEquals(0, result.getDegreeOfSickLeaveGroups().get(0).getQuantity());
        Assert.assertEquals(2, result.getDegreeOfSickLeaveGroups().get(3).getQuantity());
        Assert.assertEquals(0, result.getDegreeOfSickLeaveGroups().get(0).getAlternation());
        Assert.assertEquals(0, result.getDegreeOfSickLeaveGroups().get(3).getAlternation());

        Assert.assertEquals(2, result.getSickLeaveLengthGroups().get(0).getQuantity());
        Assert.assertEquals(0, result.getSickLeaveLengthGroups().get(1).getQuantity());

        Assert.assertEquals(0, result.getLongSickLeavesTotal());
        Assert.assertEquals(0, result.getLongSickLeavesAlternation());

        Assert.assertEquals(1, result.getPerCounty().get(0).getQuantity());
        Assert.assertEquals(0, result.getPerCounty().get(0).getAlternation());
        Assert.assertEquals(2, result.getPerCounty().get(1).getQuantity());
        Assert.assertEquals(100, result.getPerCounty().get(1).getAlternation());
    }

    @Test
    public void sortWithCollationTest() {
        List<OverviewChartRowExtended> rows = new ArrayList<>();
        rows.add(new OverviewChartRowExtended("oOo", 0, 0));
        rows.add(new OverviewChartRowExtended("oöo", 0, 0));
        rows.add(new OverviewChartRowExtended("Åao", 0, 0));
        rows.add(new OverviewChartRowExtended("AOo", 0, 0));

        sortWithCollation(rows);

        Assert.assertEquals("AOo", rows.get(0).getName());
        Assert.assertEquals("oOo", rows.get(1).getName());
        Assert.assertEquals("oöo", rows.get(2).getName());
        Assert.assertEquals("Åao", rows.get(3).getName());
    }

    // CHECKSTYLE:On MagicNumber

}
