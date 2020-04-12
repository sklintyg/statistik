/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.query;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

import com.google.common.collect.HashMultiset;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public class SjukskrivningsgradQueryTest {

    @Captor
    private ArgumentCaptor<CounterFunction<Integer>> counterCaptor;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetSjukskrivningsgradCountingAllDegreesOnlyCountDistinctDegrees() throws Exception {
        //Given
        final Clock clock = Clock.systemDefaultZone();
        final Aisle aisle = Mockito.mock(Aisle.class);
        final SjukfallUtil sjukfallUtil = Mockito.mock(SjukfallUtil.class);
        final FilterPredicates filter = SjukfallUtil.ALL_ENHETER;
        final LocalDate start = LocalDate.now(clock);
        final int periods = 1;
        final int periodSize = 1;
        Mockito.doReturn(null).when(sjukfallUtil)
            .calculateKonDataResponse(eq(aisle), eq(filter), eq(start), eq(periods), eq(periodSize), eq(SjukskrivningsgradQuery.GRAD_LABEL),
                eq(SjukskrivningsgradQuery.GRAD), counterCaptor.capture());

        final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
        Mockito.when(sjukfall.getSjukskrivningsgrader()).thenReturn(Arrays.asList(1, 2, 3, 2, 1));
        Mockito.when(sjukfall.getSjukskrivningsgrad()).thenReturn(6);
        final HashMultiset<Integer> counter = HashMultiset.create();

        //When
        SjukskrivningsgradQuery.getSjukskrivningsgrad(aisle, filter, start, periods, periodSize, sjukfallUtil, true);
        counterCaptor.getValue().addCount(sjukfall, counter);

        //Then
        assertEquals(3, counter.size());
    }

}
