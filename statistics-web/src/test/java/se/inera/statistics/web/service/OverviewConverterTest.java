package se.inera.statistics.web.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.NumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;

public class OverviewConverterTest {

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void converterTestEmpty() {
        //Given
        int casesPerMonthProportionMale = 0;
        int casesPerMonthProportionFemale = 1;
        int casesPerMonthAlteration = 2;
        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ArrayList<OverviewChartRowExtended> degreeOfSickLeaveGroups = new ArrayList<OverviewChartRowExtended>();
        ArrayList<OverviewChartRow> sickLeaveLengthGroups = new ArrayList<OverviewChartRow>();
        int longSickLeavesTotal = 3;
        int longSickLeavesAlternation = 4;
        ArrayList<OverviewChartRowExtended> perCounty = new ArrayList<OverviewChartRowExtended>();

        //When
        OverviewResponse resp = new OverviewResponse(casesPerMonthProportionMale, casesPerMonthProportionFemale, casesPerMonthAlteration, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthGroups, longSickLeavesTotal, longSickLeavesAlternation, perCounty);
        OverviewData data = new OverviewConverter().convert(resp);

        //Then
        assertEquals("[]", data.getAgeGroups().toString());
    }

    @Test
    public void converterTestCasesPerMonth() {
        //Given
        int casesPerMonthProportionMale = 0;
        int casesPerMonthProportionFemale = 1;
        int casesPerMonthAlteration = 2;
        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        diagnosisGroups.add(new OverviewChartRowExtended("diagName", 1, 2));
        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ageGroups.add(new OverviewChartRowExtended("ageName", 3, 4));
        ArrayList<OverviewChartRowExtended> degreeOfSickLeaveGroups = new ArrayList<OverviewChartRowExtended>();
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("degName", 5, 6));
        ArrayList<OverviewChartRow> sickLeaveLengthGroups = new ArrayList<OverviewChartRow>();
        sickLeaveLengthGroups.add(new OverviewChartRow("sickName", 7));
        int longSickLeavesTotal = 3;
        int longSickLeavesAlternation = 4;
        ArrayList<OverviewChartRowExtended> perCounty = new ArrayList<OverviewChartRowExtended>();
        perCounty.add(new OverviewChartRowExtended("countyName", 8, 9));

        //When
        OverviewResponse resp = new OverviewResponse(casesPerMonthProportionMale, casesPerMonthProportionFemale, casesPerMonthAlteration, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthGroups, longSickLeavesTotal, longSickLeavesAlternation, perCounty);
        OverviewData data = new OverviewConverter().convert(resp);

        //Then
        NumberOfCasesPerMonthOverview casesPerMonth = data.getCasesPerMonth();
        assertEquals(2, casesPerMonth.getAlteration());
        assertEquals(1, casesPerMonth.getProportionFemale());
        assertEquals(0, casesPerMonth.getProportionMale());

        List<DonutChartData> diagnosisGroupsResult = data.getDiagnosisGroups();
        assertEquals(1, diagnosisGroupsResult.size());
        assertEquals("diagName", diagnosisGroupsResult.get(0).getName());
        assertEquals(1, diagnosisGroupsResult.get(0).getQuantity());
        assertEquals(2, diagnosisGroupsResult.get(0).getAlternation());

        List<DonutChartData> ageGroupsResult = data.getAgeGroups();
        assertEquals(1, ageGroupsResult.size());
        assertEquals("ageName", ageGroupsResult.get(0).getName());
        assertEquals(3, ageGroupsResult.get(0).getQuantity());
        assertEquals(4, ageGroupsResult.get(0).getAlternation());

        List<DonutChartData> degreeGroupsResult = data.getDegreeOfSickLeaveGroups();
        assertEquals(1, degreeGroupsResult.size());
        assertEquals("degName", degreeGroupsResult.get(0).getName());
        assertEquals(5, degreeGroupsResult.get(0).getQuantity());
        assertEquals(6, degreeGroupsResult.get(0).getAlternation());

        List<DonutChartData> countyResult = data.getPerCounty();
        assertEquals(1, countyResult.size());
        assertEquals("countyName", countyResult.get(0).getName());
        assertEquals(8, countyResult.get(0).getQuantity());
        assertEquals(9, countyResult.get(0).getAlternation());

        SickLeaveLengthOverview sickGroupsResult = data.getSickLeaveLength();
        assertEquals(1, sickGroupsResult.getChartData().size());
        assertEquals("sickName", sickGroupsResult.getChartData().get(0).getName());
        assertEquals(7, sickGroupsResult.getChartData().get(0).getQuantity());
        assertEquals(4, sickGroupsResult.getLongSickLeavesAlternation());
        assertEquals(3, sickGroupsResult.getLongSickLeavesTotal());
    }

    // CHECKSTYLE:ON MagicNumber
}
