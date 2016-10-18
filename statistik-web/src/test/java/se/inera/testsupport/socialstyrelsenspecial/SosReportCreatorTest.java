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
package se.inera.testsupport.socialstyrelsenspecial;

import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class SosReportCreatorTest {

    @Test
    public void testGetLastDateOfLastYear() throws Exception {
        //Given
        DateTimeUtils.setCurrentMillisFixed(LocalDate.parse("2016-05-11").toDate().getTime());

        //When
        final LocalDate firstDateOfLastYear = SosReportCreator.getFirstDateOfLastYear();

        //Then
        assertEquals(LocalDate.parse("2015-01-01"), firstDateOfLastYear);
    }

    @Test
    public void testGetFirstDateOfLastYear() throws Exception {
        //Given
        DateTimeUtils.setCurrentMillisFixed(LocalDate.parse("2015-05-11").toDate().getTime());

        //When
        final LocalDate lastDateOfLastYear = SosReportCreator.getLastDateOfLastYear();

        //Then
        assertEquals(LocalDate.parse("2014-12-31"), lastDateOfLastYear);
    }
}
