/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import org.junit.Test;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;
import se.inera.statistics.web.model.overview.VerksamhetNumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VerksamhetOverviewConverterTest {

    private final Clock clock = Clock.systemDefaultZone();

    // CHECKSTYLE:OFF MagicNumber

    @Test
    public void converterTestEmpty() {
        //Given
        int casesPerMonthProportionMale = 0;
        int casesPerMonthProportionFemale = 1;
        OverviewKonsfordelning overviewKonsfordelningNew = new OverviewKonsfordelning(casesPerMonthProportionMale, casesPerMonthProportionFemale, new Range(clock));
        OverviewKonsfordelning overviewKonsfordelningOld = new OverviewKonsfordelning(casesPerMonthProportionMale, casesPerMonthProportionFemale, new Range(clock));
        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ArrayList<OverviewChartRowExtended> degreeOfSickLeaveGroups = new ArrayList<OverviewChartRowExtended>();
        ArrayList<OverviewChartRow> sickLeaveLengthGroups = new ArrayList<OverviewChartRow>();
        int longSickLeavesTotal = 3;
        int longSickLeavesAlternation = 4;
        final int totalCases = 5;

        //When
        VerksamhetOverviewResponse resp = new VerksamhetOverviewResponse(totalCases, overviewKonsfordelningNew, overviewKonsfordelningOld, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups,
                sickLeaveLengthGroups, longSickLeavesTotal, longSickLeavesAlternation);
        VerksamhetOverviewData data = new VerksamhetOverviewConverter().convert(resp, new Range(clock), Filter.empty(), null);

        //Then
        assertEquals("[]", data.getAgeGroups().toString());
    }

    @Test
    public void converterTestCasesPerMonth() {
        //Given
        int casesPerMonthProportionMaleNew = 0;
        int casesPerMonthProportionFemaleNew = 1;
        OverviewKonsfordelning overviewKonsfordelningNew = new OverviewKonsfordelning(casesPerMonthProportionMaleNew, casesPerMonthProportionFemaleNew, new Range(clock));
        int casesPerMonthProportionMaleOld = 2;
        int casesPerMonthProportionFemaleOld = 3;
        OverviewKonsfordelning overviewKonsfordelningOld = new OverviewKonsfordelning(casesPerMonthProportionMaleOld, casesPerMonthProportionFemaleOld, new Range(clock));
        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        diagnosisGroups.add(new OverviewChartRowExtended("diagName", 1, 2, null));
        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ageGroups.add(new OverviewChartRowExtended("ageName", 3, 4, null));
        ArrayList<OverviewChartRowExtended> degreeOfSickLeaveGroups = new ArrayList<OverviewChartRowExtended>();
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("degName", 5, 6, null));
        ArrayList<OverviewChartRow> sickLeaveLengthGroups = new ArrayList<OverviewChartRow>();
        sickLeaveLengthGroups.add(new OverviewChartRow("sickName", 7));
        int longSickLeavesTotal = 5;
        int longSickLeavesAlternation = 6;
        ArrayList<OverviewChartRowExtended> perCounty = new ArrayList<OverviewChartRowExtended>();
        perCounty.add(new OverviewChartRowExtended("countyName", 8, 9, null));

        int totalCases = 7;
        //When
        VerksamhetOverviewResponse resp = new VerksamhetOverviewResponse(totalCases, overviewKonsfordelningNew, overviewKonsfordelningOld, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups,
                sickLeaveLengthGroups, longSickLeavesTotal, longSickLeavesAlternation);
        VerksamhetOverviewData data = new VerksamhetOverviewConverter().convert(resp, new Range(clock), Filter.empty(), null);

        //Then
        assertEquals(totalCases, data.getCasesPerMonth().getTotalCases());

        VerksamhetNumberOfCasesPerMonthOverview casesPerMonth = data.getCasesPerMonth();
        assertEquals(1, casesPerMonth.getAmountFemaleNew());
        assertEquals(0, casesPerMonth.getAmountMaleNew());
        assertEquals(3, casesPerMonth.getAmountFemaleOld());
        assertEquals(2, casesPerMonth.getAmountMaleOld());

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

        SickLeaveLengthOverview sickGroupsResult = data.getSickLeaveLength();
        assertEquals(1, sickGroupsResult.getChartData().size());
        assertEquals("sickName", sickGroupsResult.getChartData().get(0).getName());
        assertEquals(7, sickGroupsResult.getChartData().get(0).getQuantity());
        assertEquals(longSickLeavesAlternation, sickGroupsResult.getLongSickLeavesAlternation());
        assertEquals(longSickLeavesTotal, sickGroupsResult.getLongSickLeavesTotal());
    }

    // CHECKSTYLE:ON MagicNumber
}
