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
package se.inera.statistics.service.warehouse;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class SjukskrivningsperiodTest {

    @Test
    public void testGetLengthOfJoinedPeriodsSingle() throws Exception {
        //When
        final Sjukskrivningsperiod sjukskrivningsperiod = new Sjukskrivningsperiod(4, 3);

        //Then
        final List<Sjukskrivningsperiod> periods = Collections.singletonList(sjukskrivningsperiod);
        assertEquals(3, Sjukskrivningsperiod.getLengthOfJoinedPeriods(periods));
    }

    @Test
    public void testGetAllDatesInPeriodNegativeLength() throws Exception {
        //When
        final Sjukskrivningsperiod sjukskrivningsperiod = new Sjukskrivningsperiod(4, -1);

        //Then
        final List<Sjukskrivningsperiod> periods = Collections.singletonList(sjukskrivningsperiod);
        assertEquals(0, Sjukskrivningsperiod.getLengthOfJoinedPeriods(periods));

    }

    @Test
    public void testGetAllDatesInPeriodLengthIsZero() throws Exception {
        //When
        final Sjukskrivningsperiod sjukskrivningsperiod = new Sjukskrivningsperiod(4, 0);

        //Then
        final List<Sjukskrivningsperiod> periods = Collections.singletonList(sjukskrivningsperiod);
        assertEquals(0, Sjukskrivningsperiod.getLengthOfJoinedPeriods(periods));
    }

    @Test
    public void testGetLengthOfJoinedPeriods() throws Exception {
        //Given
        final ArrayList<Sjukskrivningsperiod> periods = new ArrayList<>();
        periods.add(new Sjukskrivningsperiod(10, 15));
        periods.add(new Sjukskrivningsperiod(20, 30));
        periods.add(new Sjukskrivningsperiod(12, 17));
        periods.add(new Sjukskrivningsperiod(11, 13));
        periods.add(new Sjukskrivningsperiod(20, 300));
        periods.add(new Sjukskrivningsperiod(350, 1));

        //When
        final int length = Sjukskrivningsperiod.getLengthOfJoinedPeriods(periods);

        //Then
        assertEquals(311, length);
    }


}
