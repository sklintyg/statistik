package se.inera.statistics.service.report.repository;

import java.beans.Transient;
import java.io.Serializable;

import javax.persistence.Embeddable;
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

    @Embeddable
    public static final class DiagnosisGroupKey implements Serializable {
        private static final long serialVersionUID = 1L;

        private String period;
        private String hsaId;
        private String diagnosgrupp;

        public DiagnosisGroupKey() {
            // TODO Auto-generated constructor stub
        }
        
        public DiagnosisGroupKey(String period, String hsaId, String group) {
            this.period = period;
            this.hsaId = hsaId;
            this.diagnosgrupp = group;
        }

        public String getPeriod() {
            return period;
        }

        public String getHsaId() {
            return hsaId;
        }

        public String getDiagnosgrupp() {
            return diagnosgrupp;
        }

        @Override
        public int hashCode() {
            return period.hashCode() + hsaId.hashCode() + diagnosgrupp.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DiagnosisGroupKey) {
                DiagnosisGroupKey other = (DiagnosisGroupKey) obj;
                return period.equals(other.period) && hsaId.equals(other.hsaId) && diagnosgrupp.equals(other.diagnosgrupp);
            } else {
                return false;
            }
        }
    }

}
