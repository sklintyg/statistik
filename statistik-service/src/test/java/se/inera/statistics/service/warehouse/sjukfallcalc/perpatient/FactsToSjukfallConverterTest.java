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
package se.inera.statistics.service.warehouse.sjukfallcalc.perpatient;

import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class FactsToSjukfallConverterTest {

    @Test
    public void testGetSjukfallsPerPatientSimpleEmptyInput() throws Exception {
        //Given
        final ArrayList<Fact> facts = new ArrayList<>();

        //When
        final FactsToSjukfallConverter factsToSjukfallConverter = new FactsToSjukfallConverter();
        final ArrayListMultimap<Long, SjukfallExtended> result = factsToSjukfallConverter.getSjukfallsPerPatient(facts);

        //Then
        assertEquals(0, result.size());
    }

    @Test
    public void testGetSjukfallsPerPatientSimpleNullInput() throws Exception {
        //Given
        final ArrayList<Fact> facts = new ArrayList<>();

        //When
        final FactsToSjukfallConverter factsToSjukfallConverter = new FactsToSjukfallConverter();
        final ArrayListMultimap<Long, SjukfallExtended> result = factsToSjukfallConverter.getSjukfallsPerPatient(facts);

        //Then
        assertEquals(0, result.size());
    }

    @Test
    public void testGetSjukfallsPerPatientSimpleAllPatientsIncluded() throws Exception {
        //Given
        final ArrayList<Fact> facts = new ArrayList<>();
        facts.add(createFact(1, 2, 1));
        facts.add(createFact(1, 2, 2));
        facts.add(createFact(1, 2, 3));

        //When
        final FactsToSjukfallConverter factsToSjukfallConverter = new FactsToSjukfallConverter();
        final ArrayListMultimap<Long, SjukfallExtended> result = factsToSjukfallConverter.getSjukfallsPerPatient(facts);

        //Then
        assertEquals(3, result.size());
        assertTrue(result.keySet().containsAll(Arrays.asList(1L, 2L, 3L)));
    }

    @Test
    public void testGetSjukfallsPerPatientSimpleMergesIntygIntoSjukfall() throws Exception {
        //Given
        final ArrayList<Fact> facts = new ArrayList<>();
        facts.add(createFact(1, 2, 1));
        facts.add(createFact(3, 4, 1));
        facts.add(createFact(100, 200, 1));

        //When
        final FactsToSjukfallConverter factsToSjukfallConverter = new FactsToSjukfallConverter();
        final ArrayListMultimap<Long, SjukfallExtended> result = factsToSjukfallConverter.getSjukfallsPerPatient(facts);

        //Then
        assertEquals(2, result.size());
        assertEquals(1, result.get(1L).get(0).getStart());
        assertEquals(4, result.get(1L).get(0).getEnd());
        assertEquals(100, result.get(1L).get(1).getStart());
        assertEquals(200, result.get(1L).get(1).getEnd());
    }

    @Test
    public void testGetSjukfallsPerPatientWithActivePatientsFilter() throws Exception {
        //Given
        final ArrayList<Fact> facts = new ArrayList<>();
        facts.add(createFact(1, 2, 1));
        facts.add(createFact(1, 2, 2));
        facts.add(createFact(1, 2, 3));

        //When
        final FactsToSjukfallConverter factsToSjukfallConverter = new FactsToSjukfallConverter();
        final ArrayListMultimap<Long, SjukfallExtended> result = factsToSjukfallConverter.getSjukfallsPerPatient(facts, Arrays.asList(1L, 3L));

        //Then
        assertEquals(2, result.size());
        assertTrue(result.keySet().containsAll(Arrays.asList(1L, 3L)));
    }

    private Fact createFact(int startdatum, int slutdatum, int patient) {
        return new Fact(1,1,1,1,1, patient, startdatum, slutdatum,1,1,1,1,1,1,1,1,1, new int[0],1,false);
    }
}
