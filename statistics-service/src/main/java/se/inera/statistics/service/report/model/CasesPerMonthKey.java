package se.inera.statistics.service.report.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CasesPerMonthKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private String period;
    private String hsaId;

    public CasesPerMonthKey(String period, String enhet) {
        this.period = period;
        hsaId = enhet;
    }

    public CasesPerMonthKey() {
    }

    public String getPeriod() {
        return period;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CasesPerMonthKey that = (CasesPerMonthKey) o;

        if (hsaId != null ? !hsaId.equals(that.hsaId) : that.hsaId != null) {
            return false;
        }
        if (period != null ? !period.equals(that.period) : that.period != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = period != null ? period.hashCode() : 0;
        result = (2 * 2 * 2 * 2 * 2 - 1) * result + (hsaId != null ? hsaId.hashCode() : 0);
        return result;
    }
}
