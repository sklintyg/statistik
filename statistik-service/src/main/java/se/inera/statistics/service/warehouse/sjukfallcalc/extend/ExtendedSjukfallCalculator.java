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
package se.inera.statistics.service.warehouse.sjukfallcalc.extend;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.Collection;
import java.util.List;

class ExtendedSjukfallCalculator {

    private final ArrayListMultimap<Long, Fact> factsPerPatientInAisle;

    ExtendedSjukfallCalculator(List<Fact> aisle) {
        this.factsPerPatientInAisle = getFactsPerPatient(aisle);
    }

    SjukfallExtended getExtendedSjukfallStart(long patient, SjukfallExtended mergedSjukfall) {
        return getExtendedSjukfallStart(mergedSjukfall, factsPerPatientInAisle.get(patient));
    }

    private ArrayListMultimap<Long, Fact> getFactsPerPatient(Iterable<Fact> facts) {
        ArrayListMultimap<Long, Fact> factsPerPatient = ArrayListMultimap.create();
        for (Fact fact : facts) {
            factsPerPatient.put(fact.getPatient(), fact);
        }
        return factsPerPatient;
    }

    SjukfallExtended getExtendedSjukfallStart(final SjukfallExtended mergedSjukfall, Collection<Fact> allIntygForPatient) {
        final Collection<Fact> extendableIntyg = Collections2.filter(allIntygForPatient, new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return fact.getStartdatum() < mergedSjukfall.getStart() && fact.getStartdatum() + fact.getSjukskrivningslangd() + Sjukfall.MAX_GAP >= mergedSjukfall.getStart();
            }
        });
        if (extendableIntyg.isEmpty()) {
            return mergedSjukfall;
        }
        final Fact intygForExtending = extendableIntyg.iterator().next();
        final SjukfallExtended sjukfall = mergedSjukfall.extendSjukfallWithNewStart(intygForExtending);

        return getExtendedSjukfallStart(sjukfall, allIntygForPatient);
    }

}
