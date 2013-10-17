package se.inera.statistics.service.report.model;

public class OverviewSexProportion {

    private final int male;
    private final int female;
    private final Range period;

    public OverviewSexProportion(int male, int female, Range period) {
        this.male = male;
        this.female = female;
        this.period = period;
    }

    public int getMale() {
        return male;
    }

    public int getFemale() {
        return female;
    }

    public Range getPeriod() {
        return period;
    }

}
