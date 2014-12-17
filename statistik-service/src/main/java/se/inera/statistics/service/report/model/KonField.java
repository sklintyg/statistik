/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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

public class KonField {

    private final int female;
    private final int male;

    public KonField(int female, int male) {
        this.female = female;
        this.male = male;
    }

    public int getFemale() {
        return female;
    }

    public int getMale() {
        return male;
    }

    int getValue(Kon kon) {
        if (Kon.Female.equals(kon)) {
            return female;
        } else {
            return male;
        }
    }

    @Override
    public String toString() {
        return "{\"KonField\":{\"female\":" + female + ", \"male\":" + male + "}}";
    }
}
