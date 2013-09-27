package se.inera.statistics.service.report.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CasesPerMonthKey implements Serializable {
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
