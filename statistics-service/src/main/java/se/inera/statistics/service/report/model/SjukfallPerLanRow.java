package se.inera.statistics.service.report.model;

import se.inera.statistics.service.report.util.Verksamhet;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = SjukfallPerLanRow.TABLE)
public class SjukfallPerLanRow {

    public static final String TABLE = "SjukfallPerLanRow";
    @EmbeddedId
    private SjukfallPerLanKey key;
    @Enumerated(EnumType.STRING)
    private Verksamhet typ;
    private long female;
    private long male;

    public SjukfallPerLanRow() {
    }

    public SjukfallPerLanRow(String period, String hsaId, String lanId, long female, long male) {
        this.key = new SjukfallPerLanKey(period, hsaId, lanId);
        this.typ = Verksamhet.LAN;
        this.female = female;
        this.male = male;
    }

    public SjukfallPerLanRow(String period, String hsaId, String lanId, Verksamhet typ, long female, long male) {
        this.key = new SjukfallPerLanKey(period, hsaId, lanId);
        this.typ = typ;
        this.female = female;
        this.male = male;
    }

    @Transient
    public String getLanId() {
        return key.getLanId();
    }

    public SjukfallPerLanKey getKey() {
        return key;
    }

    public void setKey(SjukfallPerLanKey key) {
        this.key = key;
    }

    public String getPeriod() {
        return key.getPeriod();
    }

    public long getFemale() {
        return female;
    }

    public long getMale() {
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
