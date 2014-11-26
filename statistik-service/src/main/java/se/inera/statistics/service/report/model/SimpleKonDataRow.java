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

public class SimpleKonDataRow {

    private final String name;
    private final KonField data;

    public SimpleKonDataRow(String name, KonField data) {
        this.name = name;
        this.data = data;
    }

    public SimpleKonDataRow(String name, int female, int male) {
        this(name, new KonField(female, male));
    }

    public SimpleKonDataRow(String name, long female, long male) {
        this(name, new KonField((int) female, (int) male));
    }

    public String getName() {
        return name;
    }

    public KonField getData() {
        return data;
    }

    public Integer getDataForSex(Kon kon) {
        return data.getValue(kon);
    }

    public Integer getFemale() {
        return data.getValue(Kon.Female);
    }

    public Integer getMale() {
        return data.getValue(Kon.Male);
    }

    @Override
    public String toString() {
        return "{\"SimpleKonDataRow\":{" + "\"name\":\"" + name + '"' + ", \"data\":" + data + "}}";
    }
}
