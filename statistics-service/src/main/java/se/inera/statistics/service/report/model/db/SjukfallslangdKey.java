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
public class SjukfallslangdKey implements Serializable {
    private static final int HASH_MULTIPLIER = 31;

    private static final long serialVersionUID = 1L;
    public static final int MAGIC = 31;

    private String period;
    private String hsaId;
    private String grupp;
    private int periods;

    public SjukfallslangdKey() {
    }

    public SjukfallslangdKey(String period, String hsaId, String group, int periods) {
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

        SjukfallslangdKey that = (SjukfallslangdKey) o;

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
        return "{\"SjukfallslangdKey\":{" + "\"period\":\"" + period + "\", \"hsaId\":\"" + hsaId + "\", \"grupp\":\"" + grupp + "\", \"periods\":" + periods + "}}";
    }
}
