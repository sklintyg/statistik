package se.inera.statistics.service.report.mock;

import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VerksamhetOverviewMock implements VerksamhetOverview {

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public VerksamhetOverviewResponse getOverview(String verksamhetId, Range range) {

        ArrayList<OverviewChartRowExtended> diagnosisGroups = new ArrayList<OverviewChartRowExtended>();
        diagnosisGroups.add(new OverviewChartRowExtended("A-E G-L N Somatiska", 19583, 2));
        diagnosisGroups.add(new OverviewChartRowExtended("M - Muskuloskeletala", 20513, -4));
        diagnosisGroups.add(new OverviewChartRowExtended("F - Psykiska", 26120, 5));
        diagnosisGroups.add(new OverviewChartRowExtended("S - Skador", 16784, 3));
        diagnosisGroups.add(new OverviewChartRowExtended("O - Graviditet och förlossning", 1869, -3));

        ArrayList<OverviewChartRowExtended> ageGroups = new ArrayList<OverviewChartRowExtended>();
        ageGroups.add(new OverviewChartRowExtended("<35 år", 18, 2));
        ageGroups.add(new OverviewChartRowExtended("36-40 år", 9, -4));
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


        return new VerksamhetOverviewResponse(56, 44, 3, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthData, 105, 10, perCounty);
    }

    private int g() {
        final int maxValue = 100;
        return new Random().nextInt(maxValue);
    }
    //CHECKSTYLE:ON MagicNumber

    private List<DualSexField> randomData(int size) {
        DualSexField[] data = new DualSexField[size];
        for (int i = 0; i < size; i++) {
            data[i] = new DualSexField(g(), g());
        }
        return Arrays.asList(data);
    }
}
