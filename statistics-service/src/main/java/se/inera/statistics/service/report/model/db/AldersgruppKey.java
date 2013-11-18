package se.inera.statistics.service.report.model.db;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AldersgruppKey implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int HASHER = 31;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AldersgruppKey that = (AldersgruppKey) o;

        return periods == that.periods && grupp.equals(that.grupp) && hsaId.equals(that.hsaId) && period.equals(that.period);
    }

    @Override
    public int hashCode() {
        int result = period.hashCode();
        result = HASHER * result + hsaId.hashCode();
        result = HASHER * result + grupp.hashCode();
        result = HASHER * result + periods;
        return result;
    }

    @Override
    public String toString() {
        return "AldersgruppKey{" + "period='" + period + '\'' + ", hsaId='" + hsaId + '\'' + ", grupp='" + grupp + '\'' + ", periods=" + periods + '}';
    }
}
