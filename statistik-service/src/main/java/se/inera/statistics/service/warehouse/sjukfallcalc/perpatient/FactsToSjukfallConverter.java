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
package se.inera.statistics.service.warehouse.sjukfallcalc.perpatient;

import com.google.common.collect.ArrayListMultimap;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactsToSjukfallConverter {

    private final List<Fact> aisle;
    private ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatientInAisle;

    public FactsToSjukfallConverter(List<Fact> aisle) {
        this.aisle = aisle;
    }

    public ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Set<Long> patients) {
        if (sjukfallsPerPatientInAisle == null) {
            sjukfallsPerPatientInAisle = getSjukfallsPerPatient(aisle, patients);
            return sjukfallsPerPatientInAisle;
        }
        final Set<Long> cachedPatients = sjukfallsPerPatientInAisle.keySet();
        final HashSet<Long> nonCachedPatients = new HashSet<>(patients);
        nonCachedPatients.removeAll(cachedPatients);
        if (!nonCachedPatients.isEmpty()) {
            sjukfallsPerPatientInAisle.putAll(getSjukfallsPerPatient(aisle, nonCachedPatients));
        }
        return sjukfallsPerPatientInAisle;
    }

    ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Iterable<Fact> facts) {
        return getSjukfallsPerPatient(facts, null);
    }

    private ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Iterable<Fact> facts, Collection<Long> patientsFilter) {
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = ArrayListMultimap.create();
        for (Fact line : facts) {
            long key = line.getPatient();
            if (patientsFilter != null && !patientsFilter.contains(key)) {
                continue;
            }
            List<SjukfallExtended> sjukfallsForPatient = sjukfallsPerPatient.get(key);

            if (sjukfallsForPatient.isEmpty()) {
                SjukfallExtended sjukfall = new SjukfallExtended(line);
                sjukfallsPerPatient.put(key, sjukfall);
            } else {
                final SjukfallExtended sjukfall = sjukfallsForPatient.remove(sjukfallsForPatient.size() - 1);
                SjukfallExtended nextSjukfall = sjukfall.join(line);
                if (!nextSjukfall.isExtended()) {
                    sjukfallsPerPatient.put(key, sjukfall);
                }
                sjukfallsPerPatient.put(key, nextSjukfall);
            }
        }
        return sjukfallsPerPatient;
    }

}
