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
package se.inera.statistics.service.report.util;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import se.inera.statistics.service.report.model.Range;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public final class ReportUtil {

    private static final int NR_OF_PERIODS = 18;
    public static final List<String> PERIODS = createPeriods();
    private static final Locale SWEDEN = new Locale("SV", "se");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormat.forPattern("MMM yyyy").withLocale(SWEDEN);

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    private ReportUtil() {
    }

    private static List<String> createPeriods() {
        Locale sweden = new Locale("SV", "se");
        Calendar c = new GregorianCalendar(sweden);
        c.add(Calendar.MONTH, -NR_OF_PERIODS);
        List<String> names = new ArrayList<>();
        for (int i = 0; i < NR_OF_PERIODS; i++) {
            names.add(String.format(sweden, "%1$tb %1$tY", c));
            c.add(Calendar.MONTH, 1);
        }
        return names;
    }

    public static Range getPreviousPeriod(Range range) {
        LocalDate prevFrom = range.getFrom();
        LocalDate prevTo = range.getTo();
        Period period = new Period(prevFrom, prevTo);

        return new Range(range.getFrom().minusMonths(period.getMonths() + 1).minusYears(period.getYears()), range.getFrom().minusMonths(1));
    }

    public static String toPeriod(LocalDate date) {
        return OUTPUT_FORMATTER.print(date);
    }

    public static String toDiagramPeriod(LocalDate currentPeriod) {
        return OUTPUT_FORMATTER.print(currentPeriod);
    }

    public static Range getNextPeriod(Range range) {
        return new Range(range.getFrom().plusMonths(range.getMonths()), range.getTo().plusMonths(range.getMonths()));
    }
}
