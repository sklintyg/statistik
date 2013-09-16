package se.inera.statistics.web.model.overview;

import java.util.List;

public class OverviewData {

    private final NumberOfCasesPerMonthOverview casesPerMonth;
    private final List<DonutChartData> diagnosisGroups;

    public OverviewData(NumberOfCasesPerMonthOverview casesPerMonth, List<DonutChartData> diagnosisGroups) {
        this.casesPerMonth = casesPerMonth;
        this.diagnosisGroups = diagnosisGroups;
    }

    public NumberOfCasesPerMonthOverview getCasesPerMonth() {
        return casesPerMonth;
    }

    public List<DonutChartData> getDiagnosisGroups() {
        return diagnosisGroups;
    }
}
