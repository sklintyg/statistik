/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import org.junit.Test;

import static org.junit.Assert.*;

public class SjukskrivningsperiodTest {

    @Test
    public void testGetAllDatesInPeriod() throws Exception {
        //When
        final Sjukskrivningsperiod sjukskrivningsperiod = new Sjukskrivningsperiod(4, 3);

        //Then
        assertArrayEquals(new Integer[]{4, 5, 6}, sjukskrivningsperiod.getAllDatesInPeriod().toArray(new Integer[0]));
    }

    @Test
    public void testGetAllDatesInPeriodNegativeLength() throws Exception {
        //When
        final Sjukskrivningsperiod sjukskrivningsperiod = new Sjukskrivningsperiod(4, -1);

        //Then
        assertArrayEquals(new Integer[]{}, sjukskrivningsperiod.getAllDatesInPeriod().toArray(new Integer[0]));
    }

    @Test
    public void testGetAllDatesInPeriodLengthIsZero() throws Exception {
        //When
        final Sjukskrivningsperiod sjukskrivningsperiod = new Sjukskrivningsperiod(4, 0);

        //Then
        assertArrayEquals(new Integer[]{}, sjukskrivningsperiod.getAllDatesInPeriod().toArray(new Integer[0]));
    }

}
