package se.inera.statistics.service.report.repository;

import java.beans.Transient;
import java.io.Serializable;

import javax.persistence.*;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = DiagnosisGroupData.TABLE)
public class DiagnosisGroupData {

    public static final String TABLE = "DiagnosisGroupData";
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
