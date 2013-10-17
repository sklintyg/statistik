package se.inera.statistics.web.model.overview;

public class VerksamhetNumberOfCasesPerMonthOverview {

    private final int proportionMaleNew;
    private final int proportionFemaleNew;
    private final int proportionMaleOld;
    private final int proportionFemaleOld;
    private final int totalCases;

    public VerksamhetNumberOfCasesPerMonthOverview(int proportionMaleNew, int proportionFemaleNew, int proportionMaleOld, int proportionFemaleOld,
            int totalCases) {
        this.proportionMaleNew = proportionMaleNew;
        this.proportionFemaleNew = proportionFemaleNew;
        this.proportionMaleOld = proportionMaleOld;
        this.proportionFemaleOld = proportionFemaleOld;
        this.totalCases = totalCases;
    }

    public int getProportionMaleNew() {
        return proportionMaleNew;
    }

    public int getProportionFemaleNew() {
        return proportionFemaleNew;
    }

    public int getProportionMaleOld() {
        return proportionMaleOld;
    }

    public int getProportionFemaleOld() {
        return proportionFemaleOld;
    }

    public int getTotalCases() {
        return totalCases;
    }

}
