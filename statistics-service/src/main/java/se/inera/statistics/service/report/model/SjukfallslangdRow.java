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

package se.inera.statistics.service.report.model;

import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdRow {

    private Verksamhet typ;

    private final String period;
    private final String hsaId;
    private final String group;
    private final int male;
    private final int female;
    private final int periods;

    public SjukfallslangdRow(String period, String hsaId, String group, int periods, Verksamhet typ, int female, int male) {
        this.period = period;
        this.periods = periods;
        this.hsaId = hsaId;
        this.group = group;
        this.male = male;
        this.female = female;
        this.typ = typ;
    }

    public SjukfallslangdRow(String period, String group, int periods, int female, int male) {
        this(period, "NATIONELL", group, periods, Verksamhet.NATIONELL, female, male);
    }

    public String getPeriod() {
        return period;
    }

    public String getGroup() {
        return group;
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

    public int getFemale() {
        return female;
    }

    public String getHsaId() {
        return hsaId;
    }

    @Override
    public String toString() {
        return "{\"SjukfallslangdRow\":{\"key\":{\"period\":\"" + period + "\"}, \"typ\":\"" + typ + "\", \"male\":" + male + ", \"female\":" + female + "}}";
    }

    public int getPeriods() {
        return periods;
    }
}
