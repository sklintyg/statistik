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

import se.inera.statistics.service.report.util.Verksamhet;

@Entity
@Table(name = CasesPerMonthRow.TABLE)
public class CasesPerMonthRow {

    public static final String TABLE = "sjukfallpermanad";
    @EmbeddedId
    private CasesPerMonthKey key;
    @Enumerated(EnumType.STRING)
    private Verksamhet typ;
    private int female;
    private int male;

    public CasesPerMonthRow() {
    }

    public CasesPerMonthRow(String period, int female, int male) {
        this.key = new CasesPerMonthKey(period, CasesPerMonthKey.NATIONELL);
        this.female = female;
        this.male = male;
        this.typ = Verksamhet.NATIONELL;
    }

    public CasesPerMonthRow(String period, String hsaId, Verksamhet typ, int female, int male) {
        this.key = new CasesPerMonthKey(period, hsaId);
        this.typ = typ;
        this.female = female;
        this.male = male;
    }

    public String getPeriod() {
        return key.getPeriod();
    }

    public int getFemale() {
        return female;
    }

    public int getMale() {
        return male;
    }

    public void setFemale(int female) {
        this.female = female;
    }

    public void setMale(int male) {
        this.male = male;
    }

    public Verksamhet getTyp() {
        return typ;
    }

    public void setTyp(Verksamhet typ) {
        this.typ = typ;
    }
}
