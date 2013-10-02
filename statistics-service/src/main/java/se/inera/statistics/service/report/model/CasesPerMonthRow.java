package se.inera.statistics.service.report.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class CasesPerMonthRow {

    @EmbeddedId
    private CasesPerMonthKey casesPerMonthKey;
    private int female;
    private int male;

    public CasesPerMonthRow() {
    }

    public CasesPerMonthRow(String period, int female, int male) {
        this.casesPerMonthKey = new CasesPerMonthKey(period, CasesPerMonthKey.NATIONELL);
        this.female = female;
        this.male = male;
    }

    public CasesPerMonthRow(String period, String hsaId, int female, int male) {
        this.casesPerMonthKey = new CasesPerMonthKey(period, hsaId);
        this.female = female;
        this.male = male;
    }

    public String getPeriod() {
        return casesPerMonthKey.getPeriod();
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
}
