package se.inera.statistics.service.report.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CasesPerMonthKey implements Serializable {
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

}
