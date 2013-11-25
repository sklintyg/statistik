package se.inera.statistics.service.report.repository;

import java.beans.Transient;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = DiagnosisSubGroupData.TABLE)
public class DiagnosisSubGroupData {

    public static final String TABLE = "diagnosundergrupp";
    @EmbeddedId
    private DiagnosundergruppKey key;

    @Enumerated(EnumType.STRING)
    private Verksamhet typ;

    private int male;
    private int female;

    public DiagnosisSubGroupData() {
    }

    public DiagnosisSubGroupData(String period, String hsaId, String group, String subgroup, Verksamhet typ, int female, int male) {
        key = new DiagnosundergruppKey(period, hsaId, group, subgroup);
        this.female = female;
        this.male = male;
        this.typ = typ;
    }

    @Transient
    public String getPeriod() {
        return key.getPeriod();
    }

    @Transient
    public String getGroup() {
        return key.getDiagnosgrupp();
    }

    @Transient
    public String getSubGroup() {
        return key.getUndergrupp();
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

    public Verksamhet getTyp() {
        return typ;
    }

    public void setTyp(Verksamhet typ) {
        this.typ = typ;
    }

}
