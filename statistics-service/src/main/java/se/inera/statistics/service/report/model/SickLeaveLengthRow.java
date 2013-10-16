package se.inera.statistics.service.report.model;

public class SickLeaveLengthRow {

    private String group;
    private int female;
    private int male;

    public SickLeaveLengthRow() {
    }

    public SickLeaveLengthRow(String group, int female, int male) {
        this.group = group;
        this.female = female;
        this.male = male;
    }

    public String getGroup() {
        return group;
    }

    public int getFemale() {
        return female;
    }

    public int getMale() {
        return male;
    }

    public void setFemale(int female) {
        this.female = female;
    }

    public void setMale(int male) {
        this.male = male;
    }

    public int getValueForSex(Sex sex) {
        if (Sex.Female.equals(sex)) {
            return female;
        } else {
            return male;
        }
    }
}
