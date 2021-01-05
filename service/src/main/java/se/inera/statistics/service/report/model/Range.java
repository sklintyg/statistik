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

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public final class Range implements Serializable {

    private static final String RANGE_SEPARATOR = "\u2013";
    private static final int DEFAULT_PERIOD = 18;
    private static final int YEAR_PERIOD = 12;
    private static final int QUARTER_PERIOD = 3;

    private static final Locale SV = new Locale("sv", "SE");

    private static final RangeFormatter FULL_MONTH_FORMAT = new RangeFormatter("MMMM");
    private static final RangeFormatter ABBREVIATED_MONTH_FORMAT = new RangeFormatter("MMM");

    private final LocalDate from;
    private final LocalDate to;

    public Range(Clock clock) {
        this(DEFAULT_PERIOD, clock);
    }

    public Range(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    private Range(int months, Clock clock) {
        to = LocalDate.now(clock).withDayOfMonth(1).minusDays(1);
        from = to.withDayOfMonth(1).minusMonths(months - 1);
    }

    public static Range createForLastMonthsExcludingCurrent(int months, Clock clock) {
        return new Range(months, clock);
    }

    public static Range createForLastMonthsIncludingCurrent(int months, Clock clock) {
        final LocalDate toDate = LocalDate.now(clock).plusMonths(1).withDayOfMonth(1).minusDays(1);
        final LocalDate fromDate = toDate.withDayOfMonth(1).minusMonths(months - 1);
        return new Range(fromDate, toDate);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    @Override
    public String toString() {
        return FULL_MONTH_FORMAT.format(from, to);
    }

    public String toStringAbbreviated() {
        return ABBREVIATED_MONTH_FORMAT.format(from, to);
    }

    public int getNumberOfMonths() {
        return (int) ChronoUnit.MONTHS.between(from, to) + 1;
    }

    public static Range year(Clock clock) {
        return new Range(YEAR_PERIOD, clock);
    }

    public static Range quarter(Clock clock) {
        return new Range(QUARTER_PERIOD, clock);
    }

    public boolean isDateInRange(LocalDate date) {
        return (date.isAfter(from) || date.isEqual(from)) && (date.isBefore(to) || date.isEqual(to));
    }

    private static final class RangeFormatter {

        private final String monthFormat;

        private RangeFormatter(String monthFormat) {
            this.monthFormat = monthFormat;
        }

        private String format(LocalDate from, LocalDate to) {
            if (from.getYear() == to.getYear()) {
                if (from.getMonthValue() == to.getMonthValue()) {
                    return formatMonthWithYear(to);
                } else {
                    return formatMonth(from) + RANGE_SEPARATOR + formatMonthWithYear(to);
                }
            } else {
                return formatMonthWithYear(from) + RANGE_SEPARATOR + formatMonthWithYear(to);
            }
        }

        // CHECKSTYLE:OFF MagicNumber
        private String formatMonthWithYear(LocalDate when) {
            String out = when.format(DateTimeFormatter.ofPattern(monthFormat + " yyyy", SV));
            if ("MMM".equals(monthFormat)) {
                out = out.substring(0, 3) + out.substring(out.indexOf(" "));
            }
            return out;
        }

        private String formatMonth(LocalDate when) {
            String out = when.format(DateTimeFormatter.ofPattern(monthFormat, SV));
            if ("MMM".equals(monthFormat)) {
                out = out.substring(0, 3);
            }
            return out;
        }
    }

    public String toStringShortMonths() {
        String fromStr = from.getMonth().getDisplayName(TextStyle.SHORT, SV).substring(0, 3);
        String toStr = to.getMonth().getDisplayName(TextStyle.SHORT, SV).substring(0, 3);
        return fromStr + "-" + toStr;
    }
    // CHECKSTYLE:ON MagicNumber
}
