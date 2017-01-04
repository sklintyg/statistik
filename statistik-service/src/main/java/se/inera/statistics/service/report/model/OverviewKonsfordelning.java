/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

/**
 *
 * Represents relative proportions of men and women.
 *
 */
public class OverviewKonsfordelning {

    public static final int FIFTY = 50;
    public static final int PERCENT = 100;
    private final int male;
    private final int female;
    private final Range period;

    public OverviewKonsfordelning(int maleAmount, int femaleAmount, Range period) {
        this.male = maleAmount;
        this.female = femaleAmount;
        this.period = period;
    }

    public int getMaleProportion() {
        if (male == 0 && female == 0) {
            return FIFTY;
        }
        return Math.round(((float) male * PERCENT) / (male + female));
    }

    public int getFemaleProportion() {
        if (male == 0 && female == 0) {
            return FIFTY;
        }
        return Math.round(((float) female * PERCENT) / (male + female));
    }

    public int getMaleAmount() {
        return male;
    }

    public int getFemaleAmount() {
        return female;
    }

    public Range getPeriod() {
        return period;
    }

    @Override
    public String toString() {
        return "{\"OverviewKonsfordelning\":{" + "\"male\":" + male + ", \"female\":" + female + ", \"period\":\"" + period + "\"}}";
    }
}
