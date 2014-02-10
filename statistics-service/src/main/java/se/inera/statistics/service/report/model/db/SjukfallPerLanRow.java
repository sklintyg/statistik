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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = SjukfallPerLanRow.TABLE)
public class SjukfallPerLanRow {

    public static final String TABLE = "sjukfallperlan";
    @EmbeddedId
    private SjukfallPerLanKey key;
    private long female;
    private long male;

    public SjukfallPerLanRow() {
    }

    public SjukfallPerLanRow(String period, String hsaId, String lanId, long female, long male) {
        this.key = new SjukfallPerLanKey(period, hsaId, lanId);
        this.female = female;
        this.male = male;
    }

    @Transient
    public String getLanId() {
        return key.getLanId();
    }

    public SjukfallPerLanKey getKey() {
        return key;
    }

    public void setKey(SjukfallPerLanKey key) {
        this.key = key;
    }

    public String getPeriod() {
        return key.getPeriod();
    }

    public long getFemale() {
        return female;
    }

    public long getMale() {
        return male;
    }

    public void setFemale(int female) {
        this.female = female;
    }

    public void setMale(int male) {
        this.male = male;
    }
}
