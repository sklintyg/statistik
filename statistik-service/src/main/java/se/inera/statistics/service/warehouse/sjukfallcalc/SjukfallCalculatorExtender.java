/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.sjukfallcalc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;
import se.inera.statistics.service.warehouse.sjukfallcalc.extend.SjukfallMerger;
import se.inera.statistics.service.warehouse.sjukfallcalc.perpatient.FactsToSjukfallConverter;
import se.inera.statistics.service.warehouse.sjukfallcalc.perpatient.FactsToSjukfallConverterForAisle;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SjukfallCalculatorExtender {

    private final SjukfallMerger sjukfallMerger;
    private final FactsToSjukfallConverterForAisle factsToSjukfallConverterForAisle;

    SjukfallCalculatorExtender(boolean useOriginalSjukfallStart, List<Fact> aisle) {
        factsToSjukfallConverterForAisle = new FactsToSjukfallConverterForAisle(aisle, new FactsToSjukfallConverter());
        sjukfallMerger = new SjukfallMerger(aisle, useOriginalSjukfallStart);
    }

    void extendSjukfallConnectedByIntygOnOtherEnhets(Multimap<Long, SjukfallExtended> sjukfallForAvailableEnhets) {
        final Set<Long> patients = new HashSet<>(sjukfallForAvailableEnhets.keySet());
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = factsToSjukfallConverterForAisle.getSjukfallsPerPatient(patients);
        for (long patient : patients) {
            extendSjukfallConnectedByIntygOnOtherEnhetsForPatientIfNeeded(sjukfallForAvailableEnhets, sjukfallsPerPatient, patient);
        }
    }

    private void extendSjukfallConnectedByIntygOnOtherEnhetsForPatientIfNeeded(Multimap<Long, SjukfallExtended> sjukfallForAvailableEnhets, ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient, long patient) {
        final Collection<SjukfallExtended> sjukfalls = sjukfallForAvailableEnhets.get(patient);
        Collection<SjukfallExtended> sjukfallFromAllIntygForPatient = sjukfallsPerPatient.get(patient);
        final boolean noExtraSjukfallExistsOnOtherEnhet = countIntyg(sjukfalls) == countIntyg(sjukfallFromAllIntygForPatient);
        if (noExtraSjukfallExistsOnOtherEnhet) {
            return; //All intygs for patient are already included
        }
        for (SjukfallExtended sjukfallFromAllVgForPatient : sjukfallFromAllIntygForPatient) {
            sjukfallMerger.mergeAndUpdateSjukfall(patient, sjukfalls, sjukfallFromAllVgForPatient);
        }
    }

    private int countIntyg(Collection<SjukfallExtended> sjukfalls) {
        int counter = 0;
        for (SjukfallExtended sjukfall : sjukfalls) {
            counter += sjukfall.getIntygCount();
        }
        return counter;
    }

}
