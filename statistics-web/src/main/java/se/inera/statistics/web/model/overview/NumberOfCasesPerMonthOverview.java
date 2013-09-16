package se.inera.statistics.web.model.overview;

public class NumberOfCasesPerMonthOverview {

    private final int proportionMale;
    private final int proportionFemale;
    private final int alteration;

    public NumberOfCasesPerMonthOverview(int proportionMale, int proportionFemale, int alteration) {
        this.proportionMale = proportionMale;
        this.proportionFemale = proportionFemale;
        this.alteration = alteration;
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

}
