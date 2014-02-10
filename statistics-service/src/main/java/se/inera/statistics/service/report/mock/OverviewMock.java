/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.mock;

import java.util.ArrayList;

import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.OverviewSexProportion;
import se.inera.statistics.service.report.model.Range;

public class OverviewMock implements Overview {

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public OverviewResponse getOverview(Range range) {
        OverviewSexProportion sexProportion = new OverviewSexProportion(56, 44, new Range(3));

        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        diagnosisGroups.add(new OverviewChartRowExtended("A-E G-L N Somatiska", 19583, 2));
        diagnosisGroups.add(new OverviewChartRowExtended("M - Muskuloskeletala", 20513, -4));
        diagnosisGroups.add(new OverviewChartRowExtended("F - Psykiska", 26120, 5));
        diagnosisGroups.add(new OverviewChartRowExtended("S - Skador", 16784, 3));
        diagnosisGroups.add(new OverviewChartRowExtended("O - Graviditet och förlossning", 1869, -3));

        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ageGroups.add(new OverviewChartRowExtended("<21 år", 18848, 2));
        ageGroups.add(new OverviewChartRowExtended("21-25 år", 9495, -4));
        ageGroups.add(new OverviewChartRowExtended("41-45 år", 10363, 5));
        ageGroups.add(new OverviewChartRowExtended("46-50 år", 11745, 0));
        ageGroups.add(new OverviewChartRowExtended("51-55 år", 12435, -3));
        ageGroups.add(new OverviewChartRowExtended("56-60 år", 12439, -4));
        ageGroups.add(new OverviewChartRowExtended(">60 år", 11947, 5));

        ArrayList<OverviewChartRowExtended> degreeOfSickLeaveGroups = new ArrayList<OverviewChartRowExtended>();
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("25%", 2657, 15));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("50%", 13286, 0));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("75%", 6198, -15));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("100%", 66429, 15));

        ArrayList<OverviewChartRow> sickLeaveLengthData = new ArrayList<OverviewChartRow>();
        sickLeaveLengthData.add(new OverviewChartRow("<14", 9380));
        sickLeaveLengthData.add(new OverviewChartRow("15-30", 11922));
        sickLeaveLengthData.add(new OverviewChartRow("31-90", 46899));
        sickLeaveLengthData.add(new OverviewChartRow("91-180", 19539));
        sickLeaveLengthData.add(new OverviewChartRow("181-360", 779));
        sickLeaveLengthData.add(new OverviewChartRow(">360", 62));

        ArrayList<OverviewChartRowExtended> perCounty = new ArrayList<OverviewChartRowExtended>();
        perCounty.add(new OverviewChartRowExtended("Stockholms län", 19016, 2));
        perCounty.add(new OverviewChartRowExtended("Västra götalands län", 16164, -4));
        perCounty.add(new OverviewChartRowExtended("Skåne län", 10366, 5));
        perCounty.add(new OverviewChartRowExtended("Östergötlands län", 3631, 0));
        perCounty.add(new OverviewChartRowExtended("Uppsala län", 3365, -4));

        return new OverviewResponse(sexProportion, 3, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthData, 105, 10, perCounty);
    }

    //CHECKSTYLE:ON MagicNumber
}
