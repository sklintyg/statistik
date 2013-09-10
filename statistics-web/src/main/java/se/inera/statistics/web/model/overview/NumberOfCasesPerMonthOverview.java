package se.inera.statistics.web.model.overview;

public class NumberOfCasesPerMonthOverview {

    private int proportionMale;
    private int proportionFemale;
    private int alteration;

    public NumberOfCasesPerMonthOverview(int proportionMale, int proportionFemale, int alteration) {
        super();
        this.proportionMale = proportionMale;
        this.proportionFemale = proportionFemale;
        this.alteration = alteration;
    }

    public int getProportionMale() {
        return proportionMale;
    }

    public void setProportionMale(int proportionMale) {
        this.proportionMale = proportionMale;
    }

    public int getProportionFemale() {
        return proportionFemale;
    }

    public void setProportionFemale(int proportionFemale) {
        this.proportionFemale = proportionFemale;
    }

    public int getAlteration() {
        return alteration;
    }

    public void setAlteration(int alteration) {
        this.alteration = alteration;
    }

}
