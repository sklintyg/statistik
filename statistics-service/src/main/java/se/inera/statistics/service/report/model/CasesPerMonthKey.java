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

}
