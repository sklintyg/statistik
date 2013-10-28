package se.inera.statistics.web.model.overview;

import java.util.List;

public class VerksamhetOverviewData {

    private final String periodText;
    private final VerksamhetNumberOfCasesPerMonthOverview casesPerMonth;
    private final List<DonutChartData> diagnosisGroups;
    private final List<DonutChartData> ageGroups;
    private final List<DonutChartData> degreeOfSickLeaveGroups;
    private final SickLeaveLengthOverview sickLeaveLength;

    public VerksamhetOverviewData(String periodText, VerksamhetNumberOfCasesPerMonthOverview casesPerMonth, List<DonutChartData> diagnosisGroups,
            List<DonutChartData> ageGroups, List<DonutChartData> degreeOfSickLeaveGroups,
            SickLeaveLengthOverview sickLeaveLength) {
        this.periodText = periodText;
        this.casesPerMonth = casesPerMonth;
        this.diagnosisGroups = diagnosisGroups;
        this.ageGroups = ageGroups;
        this.degreeOfSickLeaveGroups = degreeOfSickLeaveGroups;
        this.sickLeaveLength = sickLeaveLength;
    }

    public String getPeriodText() {
        return periodText;
    }

    public VerksamhetNumberOfCasesPerMonthOverview getCasesPerMonth() {
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

    public SickLeaveLengthOverview getSickLeaveLength() {
        return sickLeaveLength;
    }

}
