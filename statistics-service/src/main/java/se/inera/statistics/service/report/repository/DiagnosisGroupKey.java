package se.inera.statistics.service.report.repository;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DiagnosisGroupKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;
    private String hsaId;
    private String diagnosgrupp;

    public DiagnosisGroupKey() {
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

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public void setDiagnosgrupp(String diagnosgrupp) {
        this.diagnosgrupp = diagnosgrupp;
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
