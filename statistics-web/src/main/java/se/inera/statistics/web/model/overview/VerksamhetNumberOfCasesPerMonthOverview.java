package se.inera.statistics.web.model.overview;

public class VerksamhetNumberOfCasesPerMonthOverview {

    private final int proportionMaleNew;
    private final int proportionFemaleNew;
    private final String newPeriod;
    private final int proportionMaleOld;
    private final int proportionFemaleOld;
    private final String oldPeriod;
    private final int totalCases;

    public VerksamhetNumberOfCasesPerMonthOverview(int proportionMaleNew, int proportionFemaleNew, String newPeriod, int proportionMaleOld,
            int proportionFemaleOld, String oldPeriod, int totalCases) {
        this.proportionMaleNew = proportionMaleNew;
        this.proportionFemaleNew = proportionFemaleNew;
        this.newPeriod = newPeriod;
        this.proportionMaleOld = proportionMaleOld;
        this.proportionFemaleOld = proportionFemaleOld;
        this.oldPeriod = oldPeriod;
        this.totalCases = totalCases;
    }

    public int getProportionMaleNew() {
        return proportionMaleNew;
    }

    public int getProportionFemaleNew() {
        return proportionFemaleNew;
    }

    public String getNewPeriod() {
        return newPeriod;
    }

    public int getProportionMaleOld() {
        return proportionMaleOld;
    }

    public int getProportionFemaleOld() {
        return proportionFemaleOld;
    }

    public String getOldPeriod() {
        return oldPeriod;
    }

    public int getTotalCases() {
        return totalCases;
    }

}
