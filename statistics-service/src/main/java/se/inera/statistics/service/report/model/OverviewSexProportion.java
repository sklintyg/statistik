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

    /**
     * If male + female != 100 then normalize the sum to 100.
     *
     * @param male male
     * @param female female
     */
    public OverviewSexProportion(int male, int female) {
        if (male == 0 && female == 0) {
            this.male = FIFTY;
            this.female = FIFTY;
        } else {
            this.male = (male * PERCENT) / (male + female);
            this.female = (female * PERCENT) / (male + female);
        }
    }

    public int getMale() {
        return male;
    }

    public int getFemale() {
        return female;
    }

}
