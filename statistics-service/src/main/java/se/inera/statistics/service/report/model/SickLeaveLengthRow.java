package se.inera.statistics.service.report.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = "sjukfallslangdgrupp")
public class SickLeaveLengthRow {
    @EmbeddedId
    private SickLeaveLengthKey key;
    private int male;
    private int female;

    @Enumerated(EnumType.STRING)
    private Verksamhet typ;

    public SickLeaveLengthRow() {
    }

    public SickLeaveLengthRow(String period, String hsaId, String group, int periods, Verksamhet typ, int female, int male) {
        key = new SickLeaveLengthKey(period, hsaId, group, periods);
        this.male = male;
        this.female = female;
        this.typ = typ;
    }

    public SickLeaveLengthRow(String period, String group, int periods, int female, int male) {
        key = new SickLeaveLengthKey(period, Verksamhet.NATIONELL.toString(), group, periods);
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

    public SickLeaveLengthKey getKey() {
        return key;
    }

    public void setKey(SickLeaveLengthKey key) {
        this.key = key;
    }
}
