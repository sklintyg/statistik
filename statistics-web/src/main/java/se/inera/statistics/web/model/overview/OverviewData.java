package se.inera.statistics.web.model.overview;

import java.util.List;

public class OverviewData {

    private final NumberOfCasesPerMonthOverview casesPerMonth;
    private final List<DonutChartData> diagnosisGroups;
    private final List<DonutChartData> ageGroups;
    private final List<DonutChartData> degreeOfSickLeaveGroups;

    public OverviewData(NumberOfCasesPerMonthOverview casesPerMonth, List<DonutChartData> diagnosisGroups,
            List<DonutChartData> ageGroups, List<DonutChartData> degreeOfSickLeaveGroups) {
        this.casesPerMonth = casesPerMonth;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
    }

    public NumberOfCasesPerMonthOverview getCasesPerMonth() {
        return casesPerMonth;
    }

    public List<DonutChartData> getDiagnosisGroups() {
        return diagnosisGroups;
    }

    public List<DonutChartData> getAgeGroups() {
        return ageGroups;
    }

    public List<DonutChartData> getDegreeOfSickLeaveGroups() {
        return degreeOfSickLeaveGroups;
    }

}
