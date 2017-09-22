/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.junit.Test;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ExtendedSjukfallCalculatorTest {

    @Test
    public void testFactOrderByStartdateComparator() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(70, 150, 1));
        aisle.add(createFact(50, 180, 1));
        aisle.add(createFact(60, 120, 1));

        //When
        aisle.sort(ExtendedSjukfallCalculator.FACT_ORDER_BY_STARTDATE);

        //Then
        assertEquals(50, aisle.get(0).getStartdatum());
        assertEquals(60, aisle.get(1).getStartdatum());
        assertEquals(70, aisle.get(2).getStartdatum());
    }

    @Test
    public void testGetExtendedSjukfallStartEmptyAisleWillReturnUnextendedSjukfall() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(1, 2, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertSame(sjukfall, result);
    }

    @Test
    public void testGetExtendedSjukfallStartOneIntygInAisleCanAndWillExtendStartDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(50, 150, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(50, result.getStart());
        assertEquals(200, result.getEnd());
    }

    @Test
    public void testGetExtendedSjukfallStartOneIntygInAisleWillNotExtendEndDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(150, 250, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(100, result.getStart());
        assertEquals(200, result.getEnd());
    }

    @Test
    public void testGetExtendedSjukfallStartSeveralOverlappingIntygWillExtendToEarliestStartDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(40, 85, 1));
        aisle.add(createFact(80, 150, 1));
        aisle.add(createFact(10, 55, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(10, result.getStart());
        assertEquals(200, result.getEnd());
    }

    @Test
    public void testGetExtendedSjukfallStartSeveralExtendableIntygWillExtendToEarliestStartDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(40, 185, 1));
        aisle.add(createFact(80, 150, 1));
        aisle.add(createFact(10, 155, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(10, result.getStart());
        assertEquals(200, result.getEnd());
    }

    @Test
    public void testGetExtendedSjukfallStartIntygOnOtherPatientWillNotExtendStartDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(40, 85, 2));
        aisle.add(createFact(80, 150, 1));
        aisle.add(createFact(10, 55, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(80, result.getStart());
        assertEquals(200, result.getEnd());
    }

    private Fact createFact(int startdatum, int slutdatum, int patient) {
        return new Fact(1L, 1,1,1,1,1, patient, startdatum, slutdatum,1,1,1,1,1,1,1,1,1, new int[0],1);
    }

}
