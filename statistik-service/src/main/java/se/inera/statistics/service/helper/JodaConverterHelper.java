/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.helper;

public final class JodaConverterHelper {

    private JodaConverterHelper() {
        //Constructor is expected to be empty in this helper class since it should not be instantiated
    }

    public static org.joda.time.LocalDate toJodaLocalDate(java.time.LocalDate javaLd) {
        if (javaLd == null) {
            return null;
        }
        return new org.joda.time.LocalDate(javaLd.getYear(), javaLd.getMonthValue(), javaLd.getDayOfMonth());
    }

    public static java.time.LocalDate toJavaLocalDate(org.joda.time.LocalDate joda) {
        if (joda == null) {
            return null;
        }
        return java.time.LocalDate.of(joda.getYear(), joda.getMonthOfYear(), joda.getDayOfMonth());
    }

}
