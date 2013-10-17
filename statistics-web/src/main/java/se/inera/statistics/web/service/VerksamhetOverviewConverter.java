package se.inera.statistics.web.service;

import java.util.ArrayList;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewSexProportion;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.web.model.overview.BarChartData;
import se.inera.statistics.web.model.overview.DonutChartData;
import se.inera.statistics.web.model.overview.SickLeaveLengthOverview;
import se.inera.statistics.web.model.overview.VerksamhetNumberOfCasesPerMonthOverview;
import se.inera.statistics.web.model.overview.VerksamhetOverviewData;

public class VerksamhetOverviewConverter {

    VerksamhetOverviewData convert(VerksamhetOverviewResponse resp) {
        final OverviewSexProportion casesPerMonthNew = resp.getCasesPerMonthSexProportionPreviousPeriod();
        final OverviewSexProportion casesPerMonthOld = resp.getCasesPerMonthSexProportionBeforePreviousPeriod();
        VerksamhetNumberOfCasesPerMonthOverview casesPerMonth = new VerksamhetNumberOfCasesPerMonthOverview(casesPerMonthNew.getMale(),
                casesPerMonthNew.getFemale(), casesPerMonthOld.getMale(), casesPerMonthOld.getFemale(), resp.getTotalCases());

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

        return new VerksamhetOverviewData(casesPerMonth, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLength);
    }

}
