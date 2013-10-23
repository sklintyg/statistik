package se.inera.statistics.service.report.repository;

import java.beans.Transient;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = SjukskrivningsgradData.TABLE)
public class SjukskrivningsgradData {

    public static final String TABLE = "sjukskrivningsgrad";
    @EmbeddedId
    private SjukskrivningsgradKey key;

    @Enumerated(EnumType.STRING)
    private Verksamhet typ;
    private int male;
    private int female;

    public SjukskrivningsgradData() {
    }

    public SjukskrivningsgradData(String period, String hsaId, String grad, Verksamhet typ, int female, int male) {
        key = new SjukskrivningsgradKey(period, hsaId, grad);
        this.typ = typ;
        this.female = female;
        this.male = male;
    }

    @Transient
    public String getPeriod() {
        return key.getPeriod();
    }

    @Transient
    public String getGrad() {
        return key.getGrad();
    }

    public Verksamhet getTyp() {
        return typ;
    }

    public void setTyp(Verksamhet typ) {
        this.typ = typ;
    }

    public int getFemale() {
        return female;
    }

    public int getMale() {
        return male;
    }

    public void setFemale(int l) {
        female = l;
    }

    public void setMale(int l) {
        male = l;
    }

}
