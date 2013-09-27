package se.inera.statistics.service.report.mock;

import java.util.ArrayList;

import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;

public class OverviewMock implements Overview {

    //CHECKSTYLE:OFF MagicNumber
    @Override
    public OverviewResponse getOverview() {
        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        diagnosisGroups.add(new OverviewChartRowExtended("A-E G-L N Somatiska", 140, 2));
        diagnosisGroups.add(new OverviewChartRowExtended("M - Muskuloskeletala", 140, -4));
        diagnosisGroups.add(new OverviewChartRowExtended("F - Psykiska", 40, 5));
        diagnosisGroups.add(new OverviewChartRowExtended("S - Skador", 5, 3));
        diagnosisGroups.add(new OverviewChartRowExtended("O - Graviditet och förlossning", 3, -3));

        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ageGroups.add(new OverviewChartRowExtended("<35 år", 140, 2));
        ageGroups.add(new OverviewChartRowExtended("36-40 år", 140, -4));
        ageGroups.add(new OverviewChartRowExtended("41-45 år", 40, 5));
        ageGroups.add(new OverviewChartRowExtended("46-50 år", 25, 0));
        ageGroups.add(new OverviewChartRowExtended("51-55 år", 32, -3));
        ageGroups.add(new OverviewChartRowExtended("56-60 år", 20, -4));
        ageGroups.add(new OverviewChartRowExtended(">60 år", 15, 5));

        ArrayList<OverviewChartRowExtended> degreeOfSickLeaveGroups = new ArrayList<OverviewChartRowExtended>();
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("25%", 3, 15));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("50%", 15, 0));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("75%", 7, -15));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("100%", 75, 15));

        ArrayList<OverviewChartRow> sickLeaveLengthData = new ArrayList<OverviewChartRow>();
        sickLeaveLengthData.add(new OverviewChartRow("<14", 12));
        sickLeaveLengthData.add(new OverviewChartRow("15-30", 17));
        sickLeaveLengthData.add(new OverviewChartRow("31-90", 14));
        sickLeaveLengthData.add(new OverviewChartRow("91-180", 17));
        sickLeaveLengthData.add(new OverviewChartRow("181-360", 9));
        sickLeaveLengthData.add(new OverviewChartRow(">360", 12));

        ArrayList<OverviewChartRowExtended> perCounty = new ArrayList<OverviewChartRowExtended>();
        perCounty.add(new OverviewChartRowExtended("Stockholms län", 15, 2));
        perCounty.add(new OverviewChartRowExtended("Västra götalands län", 12, -4));
        perCounty.add(new OverviewChartRowExtended("Skåne län", 6, 5));
        perCounty.add(new OverviewChartRowExtended("Östergötlands län", 5, 0));
        perCounty.add(new OverviewChartRowExtended("Uppsala län", 4, -4));

        return new OverviewResponse(56, 44, 3, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthData, 105, 10, perCounty);
    }
    //CHECKSTYLE:ON MagicNumber
}
