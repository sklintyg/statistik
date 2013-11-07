package se.inera.statistics.service.report.model.db;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SickLeaveLengthKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;
    private String hsaId;
    private String grupp;
    private int periods;

    public SickLeaveLengthKey() {
    }

    public SickLeaveLengthKey(String period, String hsaId, String group, int periods) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SickLeaveLengthKey that = (SickLeaveLengthKey) o;

        if (periods != that.periods) return false;
        if (!grupp.equals(that.grupp)) return false;
        if (!hsaId.equals(that.hsaId)) return false;
        if (!period.equals(that.period)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = period.hashCode();
        result = 31 * result + hsaId.hashCode();
        result = 31 * result + grupp.hashCode();
        result = 31 * result + periods;
        return result;
    }
}
