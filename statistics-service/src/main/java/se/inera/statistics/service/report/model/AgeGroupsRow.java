package se.inera.statistics.service.report.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "aldersgrupp")
public class AgeGroupsRow {
    public static final String NATIONELL = "nationell";

    @EmbeddedId
    private AldersgruppKey aldersgruppKey;
    private int male;
    private int female;

    public AgeGroupsRow() {
    }

    public AgeGroupsRow(String period, String hsaId, String group, int female, int male) {
        aldersgruppKey = new AldersgruppKey(period, hsaId, group);
        this.male = male;
        this.female = female;
    }

    public AgeGroupsRow(String period, String group, int female, int male) {
        aldersgruppKey = new AldersgruppKey(period, NATIONELL, group);
        this.male = male;
        this.female = female;
    }

    @Transient
    public String getPeriod() {
        return aldersgruppKey.getPeriod();
    }

    @Transient
    public String getGroup() {
        return aldersgruppKey.getGroup();
    }

    public int getMale() {
        return male;
    }

    public void setMale(int male) {
        this.male = male;
    }

    public int getFemale() {
        return female;
    }

    public void setFemale(int female) {
        this.female = female;
    }

    @Transient
    public String getHsaId() {
        return aldersgruppKey.getHsaId();
    }

    public AldersgruppKey getAldersgruppKey() {
        return aldersgruppKey;
    }

    public void setAldersgruppKey(AldersgruppKey aldersgruppKey) {
        this.aldersgruppKey = aldersgruppKey;
    }
}
