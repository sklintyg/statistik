/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
