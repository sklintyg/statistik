package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.NumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;

public class OverviewConverter {

    OverviewData convert(OverviewResponse resp, Range range) {
        Range previousPeriod = new Range(range.getFrom().minusMonths(3), range.getTo().minusMonths(3));
        NumberOfCasesPerMonthOverview casesPerMonth = new NumberOfCasesPerMonthOverview(
                resp.getCasesPerMonthSexProportion().getMaleProportion(), resp.getCasesPerMonthSexProportion().getFemaleProportion(),
                resp.getCasesPerMonthAlteration(), previousPeriod.toString());

        ArrayList<DonutChartData> diagnosisGroups = new ArrayList<>();
        for (OverviewChartRowExtended row : resp.getDiagnosisGroups()) {
            diagnosisGroups.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }

        ArrayList<DonutChartData> ageGroups = new ArrayList<>();
        for (OverviewChartRowExtended row : resp.getAgeGroups()) {
            ageGroups.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }

        ArrayList<DonutChartData> degreeOfSickLeaveGroups = new ArrayList<>();
        for (OverviewChartRowExtended row : resp.getDegreeOfSickLeaveGroups()) {
            degreeOfSickLeaveGroups.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }

        ArrayList<BarChartData> sickLeaveLengthData = new ArrayList<>();
        for (OverviewChartRow row : resp.getSickLeaveLengthGroups()) {
            sickLeaveLengthData.add(new BarChartData(row.getName(), row.getQuantity()));
        }
        SickLeaveLengthOverview sickLeaveLength = new SickLeaveLengthOverview(sickLeaveLengthData, resp.getLongSickLeavesTotal(), resp.getLongSickLeavesAlternation());

        ArrayList<DonutChartData> perCounty = new ArrayList<>();
        for (OverviewChartRowExtended row : resp.getPerCounty()) {
            perCounty.add(new DonutChartData(row.getName(), row.getQuantity(), row.getAlternation()));
        }
        Collections.sort(perCounty, new Comparator<DonutChartData>() {
            @Override
            public int compare(DonutChartData o1, DonutChartData o2) {
                return o2.getQuantity() - o1.getQuantity();
            }
        });

        return new OverviewData(range.toString(), casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLength, perCounty);
    }

}
