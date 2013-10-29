package se.inera.statistics.web.model.overview;

public class VerksamhetNumberOfCasesPerMonthOverview {

    private final int amountMaleNew;
    private final int amountFemaleNew;
    private final String newPeriod;
    private final int amountMaleOld;
    private final int amountFemaleOld;
    private final String oldPeriod;
    private final int totalCases;

    public VerksamhetNumberOfCasesPerMonthOverview(int amountMaleNew, int amountFemaleNew, String newPeriod, int amountMaleOld, int amountFemaleOld,
            String oldPeriod, int totalCases) {
        this.amountMaleNew = amountMaleNew;
        this.amountFemaleNew = amountFemaleNew;
        this.newPeriod = newPeriod;
        this.amountMaleOld = amountMaleOld;
        this.amountFemaleOld = amountFemaleOld;
        this.oldPeriod = oldPeriod;
        this.totalCases = totalCases;
    }

    public String getNewPeriod() {
        return newPeriod;
    }

    public String getOldPeriod() {
        return oldPeriod;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public int getAmountMaleNew() {
        return amountMaleNew;
    }

    public int getAmountFemaleNew() {
        return amountFemaleNew;
    }

    public int getAmountMaleOld() {
        return amountMaleOld;
    }

    public int getAmountFemaleOld() {
        return amountFemaleOld;
    }

}
