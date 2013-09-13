package se.inera.statistics.web.model.overview;

import java.util.List;

public class OverviewData {

    private NumberOfCasesPerMonthOverview casesPerMonth;
    private List<DonutChartData> diagnosisGroups;

    public OverviewData(NumberOfCasesPerMonthOverview casesPerMonth, List<DonutChartData> diagnosisGroups) {
        super();
        this.casesPerMonth = casesPerMonth;
        this.diagnosisGroups = diagnosisGroups;
    }

    public NumberOfCasesPerMonthOverview getCasesPerMonth() {
        return casesPerMonth;
    }

    public void setCasesPerMonth(NumberOfCasesPerMonthOverview casesPerMonth) {
        this.casesPerMonth = casesPerMonth;
    }

    public List<DonutChartData> getDiagnosisGroups() {
        return diagnosisGroups;
    }

    public void setDiagnosisGroups(List<DonutChartData> diagnosisGroups) {
        this.diagnosisGroups = diagnosisGroups;
    }

}
