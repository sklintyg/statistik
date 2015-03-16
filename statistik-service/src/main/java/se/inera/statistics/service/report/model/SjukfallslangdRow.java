/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.report.model;

import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdRow {

    private Verksamhet typ;

    private final String group;
    private final int male;
    private final int female;

    public SjukfallslangdRow(String group, int female, int male) {
        this.group = group;
        this.male = male;
        this.female = female;
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

    @Override
    public String toString() {
        return "{\"SjukfallslangdRow\":{\"typ\":\"" + typ + "\", \"male\":" + male + ", \"female\":" + female + "}}";
    }
}
