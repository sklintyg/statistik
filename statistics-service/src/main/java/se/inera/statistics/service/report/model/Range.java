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

import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.Months;

public final class Range {
    private static final int DEFAULT_PERIOD = 18;
    private static final int YEAR_PERIOD = 12;
    private static final int QUARTER_PERIOD = 3;
    private final LocalDate from;
    private final LocalDate to;

    public Range() {
        this(DEFAULT_PERIOD);
    }

    public Range(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public Range(int months) {
        to = new LocalDate().withDayOfMonth(1).minusMonths(1);
        from = to.minusMonths(months - 1);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public String toString() {
        return toStringWithMonthFormat("MMMM");
    }

    public String toStringAbbreviated() {
        return toStringWithMonthFormat("MMM");
    }

    private String toStringWithMonthFormat(String monthFormat) {
        Locale sv = new Locale("sv", "SE");
        if (from.getYear() == to.getYear()) {
            if (from.getMonthOfYear() == to.getMonthOfYear()) {
                return to.toString(monthFormat + " yyyy", sv);
            } else {
                return from.toString(monthFormat, sv) + "-" + to.toString(monthFormat + " yyyy", sv);
            }
        } else {
            return from.toString(monthFormat + " yyyy", sv) + "-" + to.toString(monthFormat + " yyyy", sv);
        }
    }

    public int getMonths() {
        return Months.monthsBetween(from, to).getMonths() + 1;
    }

    public static Range year() {
        return new Range(YEAR_PERIOD);
    }

    public static Range quarter() {
        return new Range(QUARTER_PERIOD);
    }
}
