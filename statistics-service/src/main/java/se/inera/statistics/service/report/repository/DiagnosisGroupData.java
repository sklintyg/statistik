package se.inera.statistics.service.report.repository;

import java.beans.Transient;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = DiagnosisGroupData.TABLE)
public class DiagnosisGroupData {

    public static final String TABLE = "diagnosisgroupdata";
    @EmbeddedId
    private DiagnosisGroupKey key;

    @Enumerated(EnumType.STRING)
    private Verksamhet typ;
    private int male;
    private int female;

    public DiagnosisGroupData() {
    }

    public DiagnosisGroupData(String period, String hsaId, String group, Verksamhet typ, int female, int male) {
        key = new DiagnosisGroupKey(period, hsaId, group);
        this.typ = typ;
        this.female = female;
        this.male = male;
    }

    @Transient
    public String getPeriod() {
        return key.getPeriod();
    }

    @Transient
    public String getGroup() {
        return key.getDiagnosgrupp();
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
