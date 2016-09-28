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

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class JodaConverterHelperTest {

    @Test
    public void testToJodaLocalDate() throws Exception {
        //Given
        final int year = 2016;
        final int month = 9;
        final int dayOfMonth = 28;
        final LocalDate javaDate = LocalDate.of(year, month, dayOfMonth);

        //When
        final org.joda.time.LocalDate jodaDate = JodaConverterHelper.toJodaLocalDate(javaDate);

        //Then
        assertEquals(year, jodaDate.getYear());
        assertEquals(month, jodaDate.getMonthOfYear());
        assertEquals(dayOfMonth, jodaDate.getDayOfMonth());
        assertEquals("2016-09-28", jodaDate.toString());
    }

    @Test
    public void testToJodaLocalDateNullInput() throws Exception {
        final org.joda.time.LocalDate jodaDate = JodaConverterHelper.toJodaLocalDate(null);
        assertNull(jodaDate);
    }

    @Test
    public void testToJavaLocalDate() throws Exception {
        //Given
        final int year = 2016;
        final int month = 9;
        final int dayOfMonth = 28;
        final org.joda.time.LocalDate jodaDate = new org.joda.time.LocalDate(year, month, dayOfMonth);

        //When
        final LocalDate javaDate = JodaConverterHelper.toJavaLocalDate(jodaDate);

        //Then
        assertEquals(year, javaDate.getYear());
        assertEquals(month, javaDate.getMonthValue());
        assertEquals(dayOfMonth, javaDate.getDayOfMonth());
        assertEquals("2016-09-28", javaDate.toString());
    }

    @Test
    public void testToJavaLocalDateNullInput() throws Exception {
        final LocalDate javaDate = JodaConverterHelper.toJavaLocalDate(null);
        assertNull(javaDate);
    }

}
