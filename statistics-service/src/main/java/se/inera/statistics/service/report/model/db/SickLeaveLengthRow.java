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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = SickLeaveLengthRow.TABLE)
public class SickLeaveLengthRow {
    public static final String TABLE = "sjukfallslangdgrupp";
    @EmbeddedId
    private SickLeaveLengthKey key;
    @Enumerated(EnumType.STRING)
    private Verksamhet typ;

    private int male;
    private int female;

    public SickLeaveLengthRow() {
    }

    public SickLeaveLengthRow(String period, String hsaId, String group, int periods, Verksamhet typ, int female, int male) {
        key = new SickLeaveLengthKey(period, hsaId, group, periods);
        this.male = male;
        this.female = female;
        this.typ = typ;
    }

    public SickLeaveLengthRow(String period, String group, int periods, int female, int male) {
        key = new SickLeaveLengthKey(period, Verksamhet.NATIONELL.toString(), group, periods);
        this.male = male;
        this.female = female;
    }

    @Transient
    public String getPeriod() {
        return key.getPeriod();
    }

    @Transient
    public String getGroup() {
        return key.getGrupp();
    }

    public Verksamhet getTyp() {
        return typ;
    }

    public void setTyp(Verksamhet typ) {
        this.typ = typ;
    }

    public int getMale() {
        return male;
    }

    public void setMale(int male) {
        this.male = male;
    }

    public int getFemale() {
        return female;
    }

    public void setFemale(int female) {
        this.female = female;
    }

    @Transient
    public String getHsaId() {
        return key.getHsaId();
    }

    public SickLeaveLengthKey getKey() {
        return key;
    }

    public void setKey(SickLeaveLengthKey key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "{\"SickLeaveLengthRow\":{\"key\":" + key + ", \"typ\":\"" + typ + "\", \"male\":" + male + ", \"female\":" + female + "}}";
    }
}
