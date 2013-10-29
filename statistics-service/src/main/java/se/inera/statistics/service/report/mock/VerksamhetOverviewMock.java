package se.inera.statistics.service.report.mock;

import java.util.ArrayList;

import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewSexProportion;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;

public class VerksamhetOverviewMock implements VerksamhetOverview {

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public VerksamhetOverviewResponse getOverview(String verksamhetId, Range range) {
        final Range newPeriod = new Range(3);
        OverviewSexProportion sexProportionNew = new OverviewSexProportion(120, 180, newPeriod);
        Range oldPeriod = new Range(newPeriod.getFrom().minusMonths(3), newPeriod.getTo().minusMonths(3));
        OverviewSexProportion sexProportionOld = new OverviewSexProportion(126, 174, oldPeriod);

        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        diagnosisGroups.add(new OverviewChartRowExtended("A-E G-L N Somatiska", 19583, 2));
        diagnosisGroups.add(new OverviewChartRowExtended("M - Muskuloskeletala", 20513, -4));
        diagnosisGroups.add(new OverviewChartRowExtended("F - Psykiska", 26120, 5));
        diagnosisGroups.add(new OverviewChartRowExtended("S - Skador", 16784, 3));
        diagnosisGroups.add(new OverviewChartRowExtended("O - Graviditet och förlossning", 1869, -3));

        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ageGroups.add(new OverviewChartRowExtended("<21 år", 18, 2));
        ageGroups.add(new OverviewChartRowExtended("21-25 år", 9, -4));
        ageGroups.add(new OverviewChartRowExtended("41-45 år", 10, 5));
        ageGroups.add(new OverviewChartRowExtended("46-50 år", 11, 0));
        ageGroups.add(new OverviewChartRowExtended("51-55 år", 12, -3));
        ageGroups.add(new OverviewChartRowExtended("56-60 år", 12, -4));
        ageGroups.add(new OverviewChartRowExtended(">60 år", 11, 5));

        ArrayList<OverviewChartRowExtended> degreeOfSickLeaveGroups = new ArrayList<OverviewChartRowExtended>();
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("25%", 2, 15));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("50%", 13, 0));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("75%", 6, -15));
        degreeOfSickLeaveGroups.add(new OverviewChartRowExtended("100%", 66, 15));

        ArrayList<OverviewChartRow> sickLeaveLengthData = new ArrayList<OverviewChartRow>();
        sickLeaveLengthData.add(new OverviewChartRow("<14", 9));
        sickLeaveLengthData.add(new OverviewChartRow("15-30", 11));
        sickLeaveLengthData.add(new OverviewChartRow("31-90", 46));
        sickLeaveLengthData.add(new OverviewChartRow("91-180", 19));
        sickLeaveLengthData.add(new OverviewChartRow("181-360", 7));
        sickLeaveLengthData.add(new OverviewChartRow(">360", 6));

        ArrayList<OverviewChartRowExtended> perCounty = new ArrayList<OverviewChartRowExtended>();
        perCounty.add(new OverviewChartRowExtended("Stockholms län", 19016, 2));
        perCounty.add(new OverviewChartRowExtended("Västra götalands län", 16164, -4));
        perCounty.add(new OverviewChartRowExtended("Skåne län", 10366, 5));
        perCounty.add(new OverviewChartRowExtended("Östergötlands län", 3631, 0));
        perCounty.add(new OverviewChartRowExtended("Uppsala län", 3365, -4));


        return new VerksamhetOverviewResponse(147, sexProportionNew, sexProportionOld, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthData, 12, 10);
    }
    //CHECKSTYLE:ON MagicNumber
}
