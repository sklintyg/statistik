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

import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.Months;

public final class Range {
    private static final String RANGE_SEPARATOR = "\u2013";
    private static final int DEFAULT_PERIOD = 18;
    private static final int YEAR_PERIOD = 12;
    private static final int QUARTER_PERIOD = 3;

    private static final Locale SV = new Locale("sv", "SE");

    private static final RangeFormatter FULL_MONTH_FORMAT = new RangeFormatter("MMMM");
    private static final RangeFormatter ABBREVIATED_MONTH_FORMAT = new RangeFormatter("MMM");

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
        to = new LocalDate().withDayOfMonth(1).minusDays(1);
        from = to.withDayOfMonth(1).minusMonths(months - 1);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public String toString() {
        return FULL_MONTH_FORMAT.format(from, to);
    }

    public String toStringAbbreviated() {
        return ABBREVIATED_MONTH_FORMAT.format(from, to);
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

    private static final class RangeFormatter {
        private final String monthFormat;

        private RangeFormatter(String monthFormat) {
            this.monthFormat = monthFormat;
        }

        private String format(LocalDate from, LocalDate to) {
            if (from.getYear() == to.getYear()) {
                if (from.getMonthOfYear() == to.getMonthOfYear()) {
                    return formatMonthWithYear(to);
                } else {
                    return formatMonth(from) + RANGE_SEPARATOR + formatMonthWithYear(to);
                }
            } else {
                return formatMonthWithYear(from) + RANGE_SEPARATOR + formatMonthWithYear(to);
            }
        }
        private String formatMonthWithYear(LocalDate when) {
            return when.toString(monthFormat + " yyyy", SV);
        }

        private String formatMonth(LocalDate when) {
            return when.toString(monthFormat, SV);
        }
    }
}
