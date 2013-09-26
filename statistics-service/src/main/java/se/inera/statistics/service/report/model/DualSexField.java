package se.inera.statistics.service.report.model;

public class DualSexField {

    private final int female;
    private final int male;

    public DualSexField(int female, int male) {
        this.female = female;
        this.male = male;
    }

    public int getFemale() {
        return female;
    }

    public int getMale() {
        return male;
    }
    
    int getValue(Sex sex){
        if (Sex.Female.equals(sex)){
            return female;
        } else {
            return male;
        }
    }

}
