package se.inera.statistics.web.model.overview;

public class NumberOfCasesPerMonthOverview {

    private final int proportionMale;
    private final int proportionFemale;
    private final int alteration;
    private final String previousPeriodText;

    public NumberOfCasesPerMonthOverview(int proportionMale, int proportionFemale, int alteration, String previousPeriodText) {
        this.proportionMale = proportionMale;
        this.proportionFemale = proportionFemale;
        this.alteration = alteration;
        this.previousPeriodText = previousPeriodText;
    }

    public int getProportionMale() {
        return proportionMale;
    }

    public int getProportionFemale() {
        return proportionFemale;
    }

    public int getAlteration() {
        return alteration;
    }

    public String getPreviousPeriodText() {
        return previousPeriodText;
    }

}
