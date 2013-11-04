package se.inera.statistics.service.report.model;

/**
 * 
 * Represents relative proportions of men and women.
 * 
 */
public class OverviewSexProportion {

    public static final int FIFTY = 50;
    public static final int PERCENT = 100;
    private final int male;
    private final int female;
    private final Range period;

    public OverviewSexProportion(int maleAmount, int femaleAmount, Range period) {
        this.male = maleAmount;
        this.female = femaleAmount;
        this.period = period;
    }

    public int getMaleProportion() {
        if (male == 0 && female == 0) {
            return FIFTY;
        }
        return Math.round(((float) male * PERCENT) / (male + female));
    }

    public int getFemaleProportion() {
        if (male == 0 && female == 0) {
            return FIFTY;
        }
        return Math.round(((float) female * PERCENT) / (male + female));
    }

    public int getMaleAmount() {
        return male;
    }

    public int getFemaleAmount() {
        return female;
    }

    public Range getPeriod() {
        return period;
    }

}
