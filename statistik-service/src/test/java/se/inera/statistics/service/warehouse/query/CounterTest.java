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
package se.inera.statistics.service.warehouse.query;

import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import static org.junit.Assert.*;

public class CounterTest {

    @Test
    public void testCompareTo1() throws Exception {
        //Given
        final Counter<String> counter1 = new Counter<>("1");
        counter1.increase(createSjukfall(Kon.Female));
        final Counter<String> counter2 = new Counter<>("2");
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));

        //When
        final int i = counter1.compareTo(counter2);

        //Then
        assertTrue(i > 0);
    }

    @Test
    public void testCompareTo2() throws Exception {
        //Given
        final Counter<String> counter1 = new Counter<>("1");
        counter1.increase(createSjukfall(Kon.Female));
        final Counter<String> counter2 = new Counter<>("2");
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));

        //When
        final int i = counter2.compareTo(counter1);

        //Then
        assertTrue(i < 0);
    }

    @Test
    public void testCompareToSameEquals() throws Exception {
        //Given
        final Counter<String> counter = new Counter<>("2");
        counter.increase(createSjukfall(Kon.Female));
        counter.increase(createSjukfall(Kon.Male));
        counter.increase(createSjukfall(Kon.Male));
        counter.increase(createSjukfall(Kon.Male));

        //When
        final int i = counter.compareTo(counter);

        //Then
        assertTrue(i == 0);
    }

    private Sjukfall createSjukfall(Kon kon) {
        final SjukfallExtended sjukfall = new SjukfallExtended(new Fact(1,1,1,1,1,1,1,1,kon.getNumberRepresentation(),1,1,1,1,1,1,1,1,new int[0],1,false));
        return Sjukfall.create(sjukfall);
    }
}
