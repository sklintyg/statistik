package se.inera.statistics.service.report.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SjukfallPerLanKey implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NATIONELL = "nationell";

    private String period;
    private String hsaId;
    private String lanId;

    public SjukfallPerLanKey(String period, String enhet, String lan) {
        this.period = period;
        hsaId = enhet;
        lanId = lan;
    }

    public SjukfallPerLanKey() {
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

    public String getLanId() {
        return lanId;
    }

    public void setLanId(String lanId) {
        this.lanId = lanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SjukfallPerLanKey that = (SjukfallPerLanKey) o;

        if (hsaId != null ? !hsaId.equals(that.hsaId) : that.hsaId != null) {
            return false;
        } else if (period != null ? !period.equals(that.period) : that.period != null) {
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
