package se.inera.statistics.service.report.model.db;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AldersgruppKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;
    private String hsaId;
    private String grupp;
    private int periods;

    public AldersgruppKey() {
    }

    public AldersgruppKey(String period, String hsaId, String group, int periods) {
        this.period = period;
        this.hsaId = hsaId;
        this.grupp = group;
        this.periods = periods;
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

    public String getGrupp() {
        return grupp;
    }

    public void setGrupp(String group) {
        this.grupp = group;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }
}
