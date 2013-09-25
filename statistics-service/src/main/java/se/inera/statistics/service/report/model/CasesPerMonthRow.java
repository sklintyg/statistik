package se.inera.statistics.service.report.model;

public class CasesPerMonthRow {

    private final String period;
    private final int female;
    private final int male;

    public CasesPerMonthRow(String period, int female, int male) {
        this.period = period;
        this.female = female;
        this.male = male;
    }

    public String getPeriod() {
        return period;
    }

    public int getFemale() {
        return female;
    }

    public int getMale() {
        return male;
    }

}
