/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import se.inera.statistics.service.report.model.Range;

public final class ReportUtil {

    private static final Locale SWEDEN = new Locale("SV", "se");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy").withLocale(SWEDEN);

    private ReportUtil() {
    }

    public static Range getPreviousPeriod(Range range) {
        LocalDate prevFrom = range.getFrom();
        LocalDate prevTo = range.getTo();
        Period period = Period.between(prevFrom, prevTo);

        return new Range(range.getFrom().minusMonths(period.getMonths() + 1).minusYears(period.getYears()), range.getFrom().minusMonths(1));
    }

    public static Range getPreviousOverviewPeriod(Range range) {
        return new Range(range.getFrom().minusYears(1), range.getTo().minusYears(1));
    }

    public static String toPeriod(LocalDate date) {
        return OUTPUT_FORMATTER.format(date);
    }

    public static String toDiagramPeriod(LocalDate currentPeriod) {
        return OUTPUT_FORMATTER.format(currentPeriod);
    }

    static Range getNextPeriod(Range range) {
        return new Range(range.getFrom().plusMonths(range.getNumberOfMonths()), range.getTo().plusMonths(range.getNumberOfMonths()));
    }
}
