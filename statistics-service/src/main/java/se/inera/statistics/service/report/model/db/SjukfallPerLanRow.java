package se.inera.statistics.service.report.model.db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = SjukfallPerLanRow.TABLE)
public class SjukfallPerLanRow {

    public static final String TABLE = "sjukfallperlan";
    @EmbeddedId
    private SjukfallPerLanKey key;
    private long female;
    private long male;

    public SjukfallPerLanRow() {
    }

    public SjukfallPerLanRow(String period, String hsaId, String lanId, long female, long male) {
        this.key = new SjukfallPerLanKey(period, hsaId, lanId);
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
}
