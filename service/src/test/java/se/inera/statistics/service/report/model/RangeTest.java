/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;

import java.time.Clock;
import java.time.LocalDate;
import org.junit.Test;

public class RangeTest {

    private Clock clock = Clock.systemDefaultZone();

    @Test
    public void testToStringWithToAndFromOnSameYear() {
        final Range range = new Range(LocalDate.of(2013, 10, 01), LocalDate.of(2013, 12, 01));
        assertEquals("oktober\u2013december 2013", range.toString());
    }

    @Test
    public void testToStringWithToAndFromOnDifferentYears() {
        final Range range = new Range(LocalDate.of(2013, 10, 01), LocalDate.of(2014, 12, 01));
        assertEquals("oktober 2013\u2013december 2014", range.toString());
    }

    @Test
    public void testToStringAbbreviatedWithToAndFromOnSameYear() {
        final Range range = new Range(LocalDate.of(2013, 10, 01), LocalDate.of(2013, 12, 01));
        assertEquals("okt\u2013dec 2013", range.toStringAbbreviated());
    }

    @Test
    public void testToStringAbbreviatedWithToAndFromOnSameYearMarchMay() {
        final Range range = new Range(LocalDate.of(2013, 3, 01), LocalDate.of(2013, 5, 01));
        assertEquals("mar\u2013maj 2013", range.toStringAbbreviated());
    }

    @Test
    public void testToStringAbbreviatedWithToAndFromOnDifferentYears() {
        final Range range = new Range(LocalDate.of(2013, 10, 01), LocalDate.of(2014, 12, 01));
        assertEquals("okt 2013\u2013dec 2014", range.toStringAbbreviated());
    }

    @Test
    public void testToStringAbbreviatedWithToAndFromOnDifferentYearsMarchMay() {
        final Range range = new Range(LocalDate.of(2013, 5, 01), LocalDate.of(2014, 3, 01));
        assertEquals("maj 2013\u2013mar 2014", range.toStringAbbreviated());
    }

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void defaultRangeIs18Months() {
        Range range = new Range(clock);
        assertEquals(18, range.getNumberOfMonths());
    }

    @Test
    public void settingRangeMonths() {
        final Range range = Range.createForLastMonthsExcludingCurrent(3, clock);
        assertEquals(3, range.getNumberOfMonths());
    }
    // CHECKSTYLE:ON MagicNumber

    @Test
    public void settingRangeMonthsCorrectStartAndEnd() {
        //When
        final Range range = Range.createForLastMonthsExcludingCurrent(3, clock);

        //Then
        final LocalDate now = LocalDate.now(clock);
        final int expectedFrom = now.minusMonths(3).withDayOfMonth(1).getDayOfYear();
        assertEquals(expectedFrom, range.getFrom().getDayOfYear());
        final int expectedTo = now.withDayOfMonth(1).minusDays(1).getDayOfYear();
        assertEquals(expectedTo, range.getTo().getDayOfYear());
    }

    @Test
    public void settingRangeMonthsIncludingCurrentCorrectStartAndEnd() {
        //When
        final Range range = Range.createForLastMonthsIncludingCurrent(3, clock);

        //Then
        final LocalDate now = LocalDate.now(clock);
        final int expectedFrom = now.minusMonths(2).withDayOfMonth(1).getDayOfYear();
        assertEquals(expectedFrom, range.getFrom().getDayOfYear());
        final int expectedTo = now.plusMonths(1).withDayOfMonth(1).minusDays(1).getDayOfYear();
        assertEquals(expectedTo, range.getTo().getDayOfYear());
    }

}
