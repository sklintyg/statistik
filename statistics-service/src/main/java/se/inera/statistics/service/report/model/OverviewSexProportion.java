package se.inera.statistics.service.report.model;

public class OverviewSexProportion {

    private final int male;
    private final int female;

    public OverviewSexProportion(int male, int female) {
        this.male = male;
        this.female = female;
    }

    public int getMale() {
        return male;
    }

    public int getFemale() {
        return female;
    }

}
