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
package se.inera.statistics.service.warehouse.sjukfallcalc.perpatient;

import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FactsToSjukfallConverterForAisleTest {

    @Test
    public void testGetSjukfallsPerPatientEmpytAile() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();

        //When
        final FactsToSjukfallConverterForAisle factsToSjukfallConverter = new FactsToSjukfallConverterForAisle(aisle, new FactsToSjukfallConverter());
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = factsToSjukfallConverter.getSjukfallsPerPatient(new HashSet<Long>(Arrays.asList(1L, 2L, 3L)));

        //Then
        assertEquals(0, sjukfallsPerPatient.size());
    }

    @Test
    public void testGetSjukfallsPerPatientEmptyPatiensList() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(1,2, 1));
        aisle.add(createFact(1,2, 2));
        aisle.add(createFact(1,2, 3));
        aisle.add(createFact(1,2, 4));
        final HashSet<Long> patients = new HashSet<>();

        //When
        final FactsToSjukfallConverterForAisle factsToSjukfallConverter = new FactsToSjukfallConverterForAisle(aisle, new FactsToSjukfallConverter());
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = factsToSjukfallConverter.getSjukfallsPerPatient(patients);

        //Then
        assertEquals(0, sjukfallsPerPatient.size());
    }

    @Test
    public void testGetSjukfallsPerPatient() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(1,2, 1));
        aisle.add(createFact(1,2, 2));
        aisle.add(createFact(1,2, 3));
        aisle.add(createFact(1,2, 4));
        final HashSet<Long> patients = new HashSet<>(Arrays.asList(1L, 2L, 3L));

        //When
        final FactsToSjukfallConverterForAisle factsToSjukfallConverter = new FactsToSjukfallConverterForAisle(aisle, new FactsToSjukfallConverter());
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = factsToSjukfallConverter.getSjukfallsPerPatient(patients);

        //Then
        assertEquals(3, sjukfallsPerPatient.size());
        assertTrue(sjukfallsPerPatient.containsKey(1L));
        assertTrue(sjukfallsPerPatient.containsKey(2L));
        assertTrue(sjukfallsPerPatient.containsKey(3L));
        assertFalse(sjukfallsPerPatient.containsKey(4L));
    }

    @Test
    public void testGetSjukfallsPerPatientSecondInvokationAlsoReturnCachedResult() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(1,2, 1));
        aisle.add(createFact(1,2, 2));
        aisle.add(createFact(1,2, 3));
        aisle.add(createFact(1,2, 4));
        final HashSet<Long> patients1 = new HashSet<>(Arrays.asList(1L, 2L));
        final HashSet<Long> patients2 = new HashSet<>(Arrays.asList(2L, 4L));

        //When
        final FactsToSjukfallConverterForAisle factsToSjukfallConverter = new FactsToSjukfallConverterForAisle(aisle, new FactsToSjukfallConverter());
        factsToSjukfallConverter.getSjukfallsPerPatient(patients1);
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = factsToSjukfallConverter.getSjukfallsPerPatient(patients2);

        //Then
        assertEquals(3, sjukfallsPerPatient.size());
        assertTrue(sjukfallsPerPatient.containsKey(1L));
        assertTrue(sjukfallsPerPatient.containsKey(2L));
        assertFalse(sjukfallsPerPatient.containsKey(3L));
        assertTrue(sjukfallsPerPatient.containsKey(4L));
    }

    private Fact createFact(int startdatum, int slutdatum, int patient) {
        return new Fact(1L, 1,1,1,1,1, patient, startdatum, slutdatum,1,1,1,1,1,1,1,1,1, new int[0],1);
    }

}
