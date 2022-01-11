/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.sjukfallcalc.extend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FactBuilder;
import se.inera.statistics.service.warehouse.SjukfallExtended;

public class SjukfallMergeHelperTest {

    @Test
    public void testMergeAllSjukfallInListNullInput() throws Exception {
        //Given
        final List<SjukfallExtended> sjukfalls = null;

        //When
        final Optional<SjukfallExtended> sjukfallExtended = SjukfallMergeHelper.mergeAllSjukfallInList(sjukfalls);

        //Then
        assertEquals(false, sjukfallExtended.isPresent());
    }

    @Test
    public void testMergeAllSjukfallInListEmptyInput() throws Exception {
        //Given
        final List<SjukfallExtended> sjukfalls = new ArrayList<>();

        //When
        final Optional<SjukfallExtended> sjukfallExtended = SjukfallMergeHelper.mergeAllSjukfallInList(sjukfalls);

        //Then
        assertEquals(false, sjukfallExtended.isPresent());
    }

    @Test
    public void testMergeAllSjukfallInListSingleInput() throws Exception {
        //Given
        final List<SjukfallExtended> sjukfalls = new ArrayList<>();
        final SjukfallExtended input = new SjukfallExtended(createFact(2, 5));
        sjukfalls.add(input);

        //When
        final SjukfallExtended sjukfallExtended = SjukfallMergeHelper.mergeAllSjukfallInList(sjukfalls).get();

        //Then
        assertSame(input, sjukfallExtended);
    }

    @Test
    public void testMergeAllSjukfallInListMergeSeveral() throws Exception {
        //Given
        final List<SjukfallExtended> sjukfalls = new ArrayList<>();
        sjukfalls.add(new SjukfallExtended(createFact(10, 15)));
        sjukfalls.add(new SjukfallExtended(createFact(100, 112)));
        sjukfalls.add(new SjukfallExtended(createFact(50, 51)));

        //When
        final SjukfallExtended result = SjukfallMergeHelper.mergeAllSjukfallInList(sjukfalls).get();

        //Then
        assertEquals(3, result.getFactCount());
        assertEquals(10, result.getStart());
        assertEquals(112, result.getEnd());
        assertEquals(21, result.getRealDays());
    }

    @Test
    public void testMergeAllSjukfallInListInputStaysTheSame() throws Exception {
        //Given
        final List<SjukfallExtended> sjukfalls = new ArrayList<>();
        sjukfalls.add(new SjukfallExtended(createFact(100, 112)));
        sjukfalls.add(new SjukfallExtended(createFact(10, 15)));
        sjukfalls.add(new SjukfallExtended(createFact(50, 51)));
        final List<SjukfallExtended> sjukfallsOriginal = new ArrayList<>(sjukfalls);

        //When
        SjukfallMergeHelper.mergeAllSjukfallInList(sjukfalls);

        //Then
        assertEquals(sjukfallsOriginal, sjukfalls);
    }

    @Test
    public void testGetFirstSjukfallNull() throws Exception {
        //Given
        final ArrayList<SjukfallExtended> sjukfalls = null;

        //When
        final Optional<SjukfallExtended> result = SjukfallMergeHelper.getFirstSjukfall(sjukfalls);

        //Then
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetFirstSjukfallEmpty() throws Exception {
        //Given
        final ArrayList<SjukfallExtended> sjukfalls = new ArrayList<>();

        //When
        final Optional<SjukfallExtended> result = SjukfallMergeHelper.getFirstSjukfall(sjukfalls);

        //Then
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetFirstSjukfall() throws Exception {
        //Given
        final ArrayList<SjukfallExtended> sjukfalls = new ArrayList<>();
        sjukfalls.add(new SjukfallExtended(createFact(2, 5)));
        sjukfalls.add(new SjukfallExtended(createFact(2, 3)));
        sjukfalls.add(new SjukfallExtended(createFact(1, 5))); //First sjukfall
        sjukfalls.add(new SjukfallExtended(createFact(10, 15)));

        //When
        final SjukfallExtended result = SjukfallMergeHelper.getFirstSjukfall(sjukfalls).get();

        //Then
        assertEquals(1, result.getStart());
        assertEquals(5, result.getEnd());
    }

    @Test
    public void testFilterSjukfallInPeriodNullSjukfalls() throws Exception {
        //Given
        final int start = 1;
        final int end = 2;
        final ArrayList<SjukfallExtended> sjukfalls = null;

        //When
        final List<SjukfallExtended> result = SjukfallMergeHelper.filterSjukfallInPeriod(start, end, sjukfalls);

        //Then
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterSjukfallInPeriodNoSjukfalls() throws Exception {
        //Given
        final int start = 1;
        final int end = 2;
        final ArrayList<SjukfallExtended> sjukfalls = new ArrayList<>();

        //When
        final List<SjukfallExtended> result = SjukfallMergeHelper.filterSjukfallInPeriod(start, end, sjukfalls);

        //Then
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterSjukfallInPeriod() throws Exception {
        //Given
        final int start = 10;
        final int end = 20;
        final ArrayList<SjukfallExtended> sjukfalls = new ArrayList<>();
        sjukfalls.add(new SjukfallExtended(createFact(1, 5)));   // Not in period
        sjukfalls.add(new SjukfallExtended(createFact(5, 10)));  // In period
        sjukfalls.add(new SjukfallExtended(createFact(11, 15))); // In period
        sjukfalls.add(new SjukfallExtended(createFact(16, 25))); // In period
        sjukfalls.add(new SjukfallExtended(createFact(20, 21))); // In period
        sjukfalls.add(new SjukfallExtended(createFact(21, 25))); // Not in period

        //When
        final List<SjukfallExtended> result = SjukfallMergeHelper.filterSjukfallInPeriod(start, end, sjukfalls);

        //Then
        assertEquals(4, result.size());
    }

    private Fact createFact(int startdatum, int slutdatum) {
        return FactBuilder.newFact(1L, 1, 1, 1, 1, 1,  1, 1, startdatum, slutdatum, 1, 1, 1, 1, 1, 1, 1, 1, 1, new int[0], 1);
    }

}
