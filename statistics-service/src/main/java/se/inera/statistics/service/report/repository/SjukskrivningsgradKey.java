package se.inera.statistics.service.report.repository;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public final class SjukskrivningsgradKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String period;
    private String hsaId;
    private int grad;

    public SjukskrivningsgradKey() {
    }

    public SjukskrivningsgradKey(String period, String hsaId, int grad) {
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

    public int getGrad() {
        return grad;
    }

    @Override
    public int hashCode() {
        return period.hashCode() + hsaId.hashCode() + grad;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SjukskrivningsgradKey) {
            SjukskrivningsgradKey other = (SjukskrivningsgradKey) obj;
            return period.equals(other.period) && hsaId.equals(other.hsaId) && grad == other.grad;
        } else {
            return false;
        }
    }
}