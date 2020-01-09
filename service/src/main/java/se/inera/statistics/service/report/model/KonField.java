/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

public class KonField {

    private int female;
    private int male;
    private Object extras;

    /**
     * Used by json parser.
     */
    private KonField() {
    }

    public KonField(int female, int male) {
        this.female = female;
        this.male = male;
    }

    public KonField(int female, int male, Object extras) {
        this.female = female;
        this.male = male;
        this.extras = extras;
    }

    public int getFemale() {
        return female;
    }

    public int getMale() {
        return male;
    }

    public Object getExtras() {
        return extras;
    }

    public int getValue(Kon kon) {
        if (Kon.FEMALE.equals(kon)) {
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
