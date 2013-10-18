package se.inera.statistics.service.report.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = "aldersgrupp")
public class AgeGroupsRow {
    @EmbeddedId
    private AldersgruppKey key;
    private int male;
    private int female;

    @Enumerated(EnumType.STRING)
    private Verksamhet typ;

    public AgeGroupsRow() {
    }

    public AgeGroupsRow(String period, String hsaId, String group, Verksamhet typ, int female, int male) {
        key = new AldersgruppKey(period, hsaId, group);
        this.male = male;
        this.female = female;
        this.typ = typ;
    }

    public AgeGroupsRow(String period, String group, int female, int male) {
        key = new AldersgruppKey(period, Verksamhet.NATIONELL.toString(), group);
        this.male = male;
        this.female = female;
    }

    @Transient
    public String getPeriod() {
        return key.getPeriod();
    }

    @Transient
    public String getGroup() {
        return key.getGroup();
    }

    public Verksamhet getTyp() {
        return typ;
    }

    public void setTyp(Verksamhet typ) {
        this.typ = typ;
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
        return key.getHsaId();
    }

    public AldersgruppKey getAldersgruppKey() {
        return key;
    }

    public void setAldersgruppKey(AldersgruppKey aldersgruppKey) {
        this.key = aldersgruppKey;
    }
}
