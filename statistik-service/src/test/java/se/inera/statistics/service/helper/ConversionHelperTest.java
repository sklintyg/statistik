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

import org.joda.time.LocalDate;
import org.junit.Test;
import se.inera.statistics.service.warehouse.WidelineConverter;

import static org.junit.Assert.*;

public class ConversionHelperTest {

    @Test
    public void testPatientIdToString() throws Exception {
        //Given
        final long id = 197503258280L;

        //When
        final String pnr = ConversionHelper.patientIdToString(id);

        //Then
        assertEquals("19750325-8280", pnr);
    }

    @Test
    public void testExtractAlder() throws Exception {
        //When
        final int alder = ConversionHelper.extractAlder("19750325-8280", new LocalDate(2015, 3, 5));

        //Then
        assertEquals(39, alder);
    }

}
