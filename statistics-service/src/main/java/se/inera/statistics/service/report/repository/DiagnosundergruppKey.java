package se.inera.statistics.service.report.repository;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DiagnosundergruppKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;
    private String hsaId;
    private String diagnosgrupp;
    private String undergrupp;

    public DiagnosundergruppKey() {
    }

    public DiagnosundergruppKey(String period, String hsaId, String group, String subgroup) {
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

    public String getUndergrupp() {
        return undergrupp;
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

    public void setUndergrupp(String undergrupp) {
        this.undergrupp = undergrupp;
    }

    @Override
    public int hashCode() {
        return period.hashCode() + hsaId.hashCode() + diagnosgrupp.hashCode() + undergrupp.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiagnosundergruppKey) {
            DiagnosundergruppKey other = (DiagnosundergruppKey) obj;
            return period.equals(other.period) && hsaId.equals(other.hsaId) && diagnosgrupp.equals(other.diagnosgrupp) && undergrupp.equals(other.undergrupp);
        } else {
            return false;
        }
    }
}
