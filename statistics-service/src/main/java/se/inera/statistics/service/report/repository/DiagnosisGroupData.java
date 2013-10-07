package se.inera.statistics.service.report.repository;

import java.beans.Transient;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class DiagnosisGroupData {

    @EmbeddedId
    private DiagnosisGroupKey diagnosisGroupKey;
    
    private int male;
    private int female;

    public DiagnosisGroupData() {
    }

    public DiagnosisGroupData(String period, String hsaId, String group, int female, int male) {
        diagnosisGroupKey = new DiagnosisGroupKey(period, hsaId, group);
        this.female = female;
        this.male = male;
    }
    
    @Transient
    public String getPeriod() {
        return diagnosisGroupKey.getPeriod();
    }

    @Transient
    public String getGroup() {
        return diagnosisGroupKey.getDiagnosgrupp();
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
