package se.inera.statistics.service.report.repository;

import java.beans.Transient;
import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
public class DiagnosisSubGroupData {

    @EmbeddedId
    private Key key;

    private int male;
    private int female;

    @Enumerated(EnumType.STRING)
    private Verksamhet typ;

    public DiagnosisSubGroupData() {
    }

    public DiagnosisSubGroupData(String period, String hsaId, String group, String subgroup, Verksamhet typ, int female, int male) {
        key = new Key(period, hsaId, group, subgroup);
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
        return key.getSubGroup();
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

    @Embeddable
    public static final class Key implements Serializable {
        private static final long serialVersionUID = 1L;

        private String period;
        private String hsaId;
        private String diagnosgrupp;
        private String undergrupp;

        public Key() {
            // TODO Auto-generated constructor stub
        }

        public Key(String period, String hsaId, String group, String subgroup) {
            this.period = period;
            this.hsaId = hsaId;
            this.diagnosgrupp = group;
            this.undergrupp = subgroup;
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

        public String getSubGroup() {
            return undergrupp;
        }

        @Override
        public int hashCode() {
            return period.hashCode() + hsaId.hashCode() + diagnosgrupp.hashCode() + undergrupp.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key other = (Key) obj;
                return period.equals(other.period) && hsaId.equals(other.hsaId) && diagnosgrupp.equals(other.diagnosgrupp) && undergrupp.equals(other.undergrupp);
            } else {
                return false;
            }
        }
    }

}
