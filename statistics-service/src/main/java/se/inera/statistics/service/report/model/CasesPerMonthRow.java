package se.inera.statistics.service.report.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
public class CasesPerMonthRow {

    @EmbeddedId
    private CasesPerMonthKey key;
    @Enumerated(EnumType.STRING)
    private Verksamhet typ;
    private int female;
    private int male;

    public CasesPerMonthRow() {
    }

    public CasesPerMonthRow(String period, int female, int male) {
        this.key = new CasesPerMonthKey(period, CasesPerMonthKey.NATIONELL);
        this.female = female;
        this.male = male;
        this.typ = Verksamhet.NATIONELL;
    }

    public CasesPerMonthRow(String period, String hsaId, Verksamhet typ, int female, int male) {
        this.key = new CasesPerMonthKey(period, hsaId);
        this.typ = typ;
        this.female = female;
        this.male = male;
    }

    public String getPeriod() {
        return key.getPeriod();
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

    public Verksamhet getTyp() {
        return typ;
    }

    public void setTyp(Verksamhet typ) {
        this.typ = typ;
    }
}
