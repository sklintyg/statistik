package se.inera.statistics.web.model.overview;


public class OverviewData {

    private NumberOfCasesPerMonthOverview casesPerMonth;

    public OverviewData(NumberOfCasesPerMonthOverview casesPerMonth) {
        super();
        this.casesPerMonth = casesPerMonth;
    }

    public NumberOfCasesPerMonthOverview getCasesPerMonth() {
        return casesPerMonth;
    }

    public void setCasesPerMonth(NumberOfCasesPerMonthOverview casesPerMonth) {
        this.casesPerMonth = casesPerMonth;
    }

}
