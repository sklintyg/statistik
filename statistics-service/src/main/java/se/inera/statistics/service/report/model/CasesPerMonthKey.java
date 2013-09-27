package se.inera.statistics.service.report.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CasesPerMonthKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private String period;
    private String hsaId;

    public CasesPerMonthKey(String period, String enhet) {
    }

    public CasesPerMonthKey() {
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }
}
