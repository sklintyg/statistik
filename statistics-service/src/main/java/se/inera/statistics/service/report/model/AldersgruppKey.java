package se.inera.statistics.service.report.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AldersgruppKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;
    private String hsaId;
    private String group;

    public AldersgruppKey() {
    }

    public AldersgruppKey(String period, String hsaId, String group) {
        this.period = period;
        this.hsaId = hsaId;
        this.group = group;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
