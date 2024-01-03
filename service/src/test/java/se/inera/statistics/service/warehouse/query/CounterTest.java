/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.FactBuilder;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallExtended;

public class CounterTest {

    @Test
    public void testGet() throws Exception {
        //Given
        final String testKey = "1";
        final Counter<String> counter = new Counter<>(testKey);

        //When
        counter.increase(createSjukfall(Kon.FEMALE));
        counter.increase(createSjukfall(Kon.MALE));
        counter.increase(createSjukfall(Kon.MALE));
        counter.increase(createSjukfall(Kon.MALE));

        //Then
        assertEquals(4, counter.getCount());
        assertEquals(3, counter.getCountMale());
        assertEquals(1, counter.getCountFemale());
        assertEquals(testKey, counter.getKey());
    }

    @Test
    public void testByTotalCount1() throws Exception {
        //Given
        final Counter<String> counter1 = new Counter<>("1");
        counter1.increase(createSjukfall(Kon.FEMALE));
        final Counter<String> counter2 = new Counter<>("2");
        counter2.increase(createSjukfall(Kon.MALE));
        counter2.increase(createSjukfall(Kon.MALE));
        counter2.increase(createSjukfall(Kon.MALE));

        //When
        final int i = Counter.byTotalCount().compare(counter1, counter2);

        //Then
        assertTrue(i > 0);
    }

    @Test
    public void testByTotalCount2() throws Exception {
        //Given
        final Counter<String> counter1 = new Counter<>("1");
        counter1.increase(createSjukfall(Kon.FEMALE));
        final Counter<String> counter2 = new Counter<>("2");
        counter2.increase(createSjukfall(Kon.MALE));
        counter2.increase(createSjukfall(Kon.MALE));
        counter2.increase(createSjukfall(Kon.MALE));

        //When
        final int i = Counter.byTotalCount().compare(counter2, counter1);

        //Then
        assertTrue(i < 0);
    }

    @Test
    public void testByTotalCountSameEquals() throws Exception {
        //Given
        final Counter<String> counter = new Counter<>("2");
        counter.increase(createSjukfall(Kon.FEMALE));
        counter.increase(createSjukfall(Kon.MALE));
        counter.increase(createSjukfall(Kon.MALE));
        counter.increase(createSjukfall(Kon.MALE));

        //When
        final int i = Counter.byTotalCount().compare(counter, counter);

        //Then
        assertTrue(i == 0);
    }

    private Sjukfall createSjukfall(Kon kon) {
        final SjukfallExtended sjukfall = new SjukfallExtended(
            FactBuilder.newFact(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1, kon.getNumberRepresentation(), 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1));
        return Sjukfall.create(sjukfall);
    }
}
