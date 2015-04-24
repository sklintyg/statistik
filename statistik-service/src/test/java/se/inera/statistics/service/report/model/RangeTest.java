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

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

public class RangeTest {

    @Test
    public void testToStringWithToAndFromOnSameYear() {
        final Range range = new Range(new LocalDate(2013, 10, 01), new LocalDate(2013, 12, 01));
        assertEquals("oktober\u2013december 2013", range.toString());
    }

    @Test
    public void testToStringWithToAndFromOnDifferentYears() {
        final Range range = new Range(new LocalDate(2013, 10, 01), new LocalDate(2014, 12, 01));
        assertEquals("oktober 2013\u2013december 2014", range.toString());
    }

    @Test
    public void testToStringAbbreviatedWithToAndFromOnSameYear() {
        final Range range = new Range(new LocalDate(2013, 10, 01), new LocalDate(2013, 12, 01));
        assertEquals("okt\u2013dec 2013", range.toStringAbbreviated());
    }

    @Test
    public void testToStringAbbreviatedWithToAndFromOnDifferentYears() {
        final Range range = new Range(new LocalDate(2013, 10, 01), new LocalDate(2014, 12, 01));
        assertEquals("okt 2013\u2013dec 2014", range.toStringAbbreviated());
    }

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void defaultRangeIs18Months() {
        Range range = new Range();
        assertEquals(18, range.getMonths());
    }

    @Test
    public void settingRangeMonths() {
        final Range range = new Range(3);
        assertEquals(3, range.getMonths());
    }
    // CHECKSTYLE:ON MagicNumber

    @Test
    public void settingRangeMonthsCorrectStartAndEnd() {
        //When
        final Range range = new Range(3);

        //Then
        final LocalDate now = new LocalDate();
        final int expectedFrom = now.minusMonths(3).withDayOfMonth(1).getDayOfYear();
        assertEquals(expectedFrom, range.getFrom().getDayOfYear());
        final int expectedTo = now.minusMonths(1).dayOfMonth().withMaximumValue().getDayOfYear();
        assertEquals(expectedTo, range.getTo().getDayOfYear());
    }

}
