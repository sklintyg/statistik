package se.inera.statistics.service.report.repository;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SjukskrivningsgradKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;
    private String hsaId;
    private String grad;

    public SjukskrivningsgradKey() {
    }

    public SjukskrivningsgradKey(String period, String hsaId, String grad) {
        this.period = period;
        this.hsaId = hsaId;
        this.grad = grad;
    }

    public String getPeriod() {
        return period;
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getGrad() {
        return grad;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public void setGrad(String grad) {
        this.grad = grad;
    }

    @Override
    public int hashCode() {
        return period.hashCode() + hsaId.hashCode() + grad.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SjukskrivningsgradKey) {
            SjukskrivningsgradKey other = (SjukskrivningsgradKey) obj;
            return period.equals(other.period) && hsaId.equals(other.hsaId) && grad.equals(other.grad);
        } else {
            return false;
        }
    }
}
