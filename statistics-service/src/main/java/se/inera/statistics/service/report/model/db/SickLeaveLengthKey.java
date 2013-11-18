package se.inera.statistics.service.report.model.db;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SickLeaveLengthKey implements Serializable {
    private static final int HASH_MULTIPLIER = 31;

    private static final long serialVersionUID = 1L;
    public static final int MAGIC = 31;

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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SickLeaveLengthKey that = (SickLeaveLengthKey) o;

        return (periods == that.periods) && grupp.equals(that.grupp) && hsaId.equals(that.hsaId) && period.equals(that.period);
    }

    @Override
    public int hashCode() {
        int result = period.hashCode();
        result = HASH_MULTIPLIER * result + hsaId.hashCode();
        result = MAGIC * result + grupp.hashCode();
        result = MAGIC * result + periods;
        return result;
    }

    @Override
    public String toString() {
        return "SickLeaveLengthKey{" + "period='" + period + '\'' + ", hsaId='" + hsaId + '\'' + ", grupp='" + grupp + '\'' + ", periods=" + periods + '}';
    }
}
