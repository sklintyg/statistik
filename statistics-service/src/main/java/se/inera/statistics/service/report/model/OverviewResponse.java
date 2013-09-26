package se.inera.statistics.service.report.model;

import java.util.List;

public class OverviewResponse {

    private final int casesPerMonthProportionMale;
    private final int casesPerMonthProportionFemale;
    private final int casesPerMonthAlteration;

    private final List<OverviewChartRowExtended> diagnosisGroups;

    private final List<OverviewChartRowExtended> ageGroups;

    private final List<OverviewChartRowExtended> degreeOfSickLeaveGroups;

    private final List<OverviewChartRow> sickLeaveLengthGroups;
    private final int longSickLeavesTotal;
    private final int longSickLeavesAlternation;

    private final List<OverviewChartRowExtended> perCounty;

    public OverviewResponse(int casesPerMonthProportionMale, int casesPerMonthProportionFemale,
            int casesPerMonthAlteration, List<OverviewChartRowExtended> diagnosisGroups,
            List<OverviewChartRowExtended> ageGroups, List<OverviewChartRowExtended> degreeOfSickLeaveGroups,
            List<OverviewChartRow> sickLeaveLengthGroups, int longSickLeavesTotal, int longSickLeavesAlternation,
            List<OverviewChartRowExtended> perCounty) {
        this.casesPerMonthProportionMale = casesPerMonthProportionMale;
        this.casesPerMonthProportionFemale = casesPerMonthProportionFemale;
        this.casesPerMonthAlteration = casesPerMonthAlteration;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
        this.sickLeaveLengthGroups = sickLeaveLengthGroups;
        this.longSickLeavesTotal = longSickLeavesTotal;
        this.longSickLeavesAlternation = longSickLeavesAlternation;
        this.perCounty = perCounty;
    }

    public int getCasesPerMonthProportionMale() {
        return casesPerMonthProportionMale;
    }

    public int getCasesPerMonthProportionFemale() {
        return casesPerMonthProportionFemale;
    }

    public int getCasesPerMonthAlteration() {
        return casesPerMonthAlteration;
    }

    public List<OverviewChartRowExtended> getDiagnosisGroups() {
        return diagnosisGroups;
    }

    public List<OverviewChartRowExtended> getAgeGroups() {
        return ageGroups;
    }

    public List<OverviewChartRowExtended> getDegreeOfSickLeaveGroups() {
        return degreeOfSickLeaveGroups;
    }

    public List<OverviewChartRow> getSickLeaveLengthGroups() {
        return sickLeaveLengthGroups;
    }

    public int getLongSickLeavesTotal() {
        return longSickLeavesTotal;
    }

    public int getLongSickLeavesAlternation() {
        return longSickLeavesAlternation;
    }

    public List<OverviewChartRowExtended> getPerCounty() {
        return perCounty;
    }

}
