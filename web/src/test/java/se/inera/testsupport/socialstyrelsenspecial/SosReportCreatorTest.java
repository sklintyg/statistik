/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.socialstyrelsenspecial;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.Assert.*;

public class SosReportCreatorTest {

    @Test
    public void testGetFirstDateOfYear() throws Exception {
        //Given
        final Clock clock = Clock.fixed(Instant.parse("2016-05-11T10:15:30.00Z"), ZoneId.systemDefault());
        final SosReportCreator sosReportCreator = new SosReportCreator(null, null, null, null, false, clock, 2015, 2015);

        //When
        final LocalDate firstDateOfLastYear = sosReportCreator.getFirstDateOfYear();

        //Then
        assertEquals(LocalDate.parse("2015-01-01"), firstDateOfLastYear);
    }

    @Test
    public void testGetLastDateOfYear() throws Exception {
        //Given
        final Clock clock = Clock.fixed(Instant.parse("2015-05-11T10:15:30.00Z"), ZoneId.systemDefault());
        final SosReportCreator sosReportCreator = new SosReportCreator(null, null, null, null, false, clock, 2014, 2014);

        //When
        final LocalDate lastDateOfLastYear = sosReportCreator.getLastDateOfYear();

        //Then
        assertEquals(LocalDate.parse("2014-12-31"), lastDateOfLastYear);
    }

}
