/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

public class FactsToSjukfallConverterForAisle {

    private final List<Fact> aisle;
    private final FactsToSjukfallConverter factsToSjukfallConverter;
    private ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatientInAisle;

    public FactsToSjukfallConverterForAisle(List<Fact> aisle, FactsToSjukfallConverter factsToSjukfallConverter) {
        this.aisle = aisle;
        this.factsToSjukfallConverter = factsToSjukfallConverter;
    }

    public ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Set<Long> patients) {
        if (sjukfallsPerPatientInAisle == null) {
            sjukfallsPerPatientInAisle = factsToSjukfallConverter.getSjukfallsPerPatient(aisle, patients);
            return sjukfallsPerPatientInAisle;
        }
        final List<Long> nonCachedPatients = patients.stream()
            .filter(aLong -> !sjukfallsPerPatientInAisle.containsKey(aLong))
            .collect(Collectors.toList());
        if (!nonCachedPatients.isEmpty()) {
            sjukfallsPerPatientInAisle.putAll(factsToSjukfallConverter.getSjukfallsPerPatient(aisle, nonCachedPatients));
        }
        return sjukfallsPerPatientInAisle;
    }

}
