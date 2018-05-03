/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

public class SjukfallMergerTest {

    @Test
    public void testMergeAndUpdateSjukfallEmptyInput() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        final long patient = 1L;
        final ArrayList<SjukfallExtended> sjukfallsFromAvailableEnhetsForPatient = new ArrayList<>();
        final SjukfallExtended sjukfallFromAllVgForPatient = new SjukfallExtended(createFact(1, 2));

        //When
        final SjukfallMerger sjukfallMerger = new SjukfallMerger(aisle);
        sjukfallMerger.mergeAndUpdateSjukfall(patient, sjukfallsFromAvailableEnhetsForPatient, sjukfallFromAllVgForPatient);

        //Then
        assertEquals(0, sjukfallsFromAvailableEnhetsForPatient.size());
    }

    @Test
    public void testMergeAndUpdateSjukfall() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(1, 9));
        final long patient = 1L;
        final ArrayList<SjukfallExtended> sjukfallsFromAvailableEnhetsForPatient = new ArrayList<>();
        sjukfallsFromAvailableEnhetsForPatient.add(new SjukfallExtended(createFact(10,20)));
        sjukfallsFromAvailableEnhetsForPatient.add(new SjukfallExtended(createFact(50,60)));
        final SjukfallExtended unchangedSjukfall = new SjukfallExtended(createFact(61, 70));
        sjukfallsFromAvailableEnhetsForPatient.add(unchangedSjukfall);
        final SjukfallExtended sjukfallFromAllVgForPatient = new SjukfallExtended(createFact(10, 60));

        //When
        final SjukfallMerger sjukfallMerger = new SjukfallMerger(aisle);
        sjukfallMerger.mergeAndUpdateSjukfall(patient, sjukfallsFromAvailableEnhetsForPatient, sjukfallFromAllVgForPatient);

        //Then
        assertEquals(2, sjukfallsFromAvailableEnhetsForPatient.size());
        assertEquals(unchangedSjukfall, sjukfallsFromAvailableEnhetsForPatient.get(0));
        assertEquals(1, sjukfallsFromAvailableEnhetsForPatient.get(1).getStart());
        assertEquals(60, sjukfallsFromAvailableEnhetsForPatient.get(1).getEnd());
    }

    private Fact createFact(int startdatum, int slutdatum) {
        return new Fact(1L, 1,1,1,1,1,1, startdatum, slutdatum,1,1,1,1,1,1,1,1,1, new int[0],1);
    }

}
