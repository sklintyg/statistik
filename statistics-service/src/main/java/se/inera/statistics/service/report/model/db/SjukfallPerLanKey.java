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

package se.inera.statistics.service.report.model.db;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SjukfallPerLanKey implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NATIONELL = "nationell";

    private String period;
    private String hsaId;
    private String lanId;
    private int periods;

    public SjukfallPerLanKey(String period, String enhet, String lan, int periods) {
        this.period = period;
        hsaId = enhet;
        lanId = lan;
        this.periods = periods;
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

        SjukfallPerLanKey that = (SjukfallPerLanKey) o;

        if (hsaId != null ? !hsaId.equals(that.hsaId) : that.hsaId != null) {
            return false;
        } else if (period != null ? !period.equals(that.period) : that.period != null) {
            return false;
        } else if (periods != that.periods) {
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
