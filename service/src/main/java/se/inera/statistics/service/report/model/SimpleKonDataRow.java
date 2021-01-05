/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.report.model;

public class SimpleKonDataRow {

    private final String name;
    private final KonField data;
    private final Object extras;

    public SimpleKonDataRow(String name, KonField data) {
        this.name = name;
        this.data = data;
        this.extras = null;
    }

    public SimpleKonDataRow(String name, int female, int male) {
        this(name, new KonField(female, male));
    }

    public SimpleKonDataRow(String name, KonField data, Object extras) {
        this.name = name;
        this.data = data;
        this.extras = extras;
    }

    public SimpleKonDataRow(String name, int female, int male, Object extras) {
        this(name, new KonField(female, male), extras);
    }

    public String getName() {
        return name;
    }

    public KonField getData() {
        return data;
    }

    public int getDataForSex(Kon kon) {
        return data.getValue(kon);
    }

    public int getFemale() {
        return data.getValue(Kon.FEMALE);
    }

    public int getMale() {
        return data.getValue(Kon.MALE);
    }

    public Object getExtras() {
        return extras;
    }

    @Override
    public String toString() {
        return "{\"SimpleKonDataRow\":{" + "\"name\":\"" + name + '"' + ", \"data\":" + data + "}}";
    }
}
