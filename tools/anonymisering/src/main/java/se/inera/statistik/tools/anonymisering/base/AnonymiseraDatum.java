/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistik.tools.anonymisering.base;

import java.time.LocalDate;
import java.util.Random;

public class AnonymiseraDatum {

    public static final int DATE_RANGE = 28;

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    public String anonymiseraDatum(String datum) {
        if (datum == null) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(datum);
            // random days from -14 to +14, but not 0
            int days = random.nextInt(DATE_RANGE) - DATE_RANGE / 2;
            if (days == 0) {
                days = DATE_RANGE / 2;
            }
            date = date.plusDays(days);
            return date.toString();
        } catch (Exception e) {
            return datum;
        }
    }
    // CHECKSTYLE:ON MagicNumber

}
