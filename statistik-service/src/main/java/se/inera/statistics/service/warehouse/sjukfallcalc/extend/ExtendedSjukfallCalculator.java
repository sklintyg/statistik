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
package se.inera.statistics.service.warehouse.sjukfallcalc.extend;

import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class ExtendedSjukfallCalculator {

    static final Comparator<Fact> FACT_ORDER_BY_STARTDATE = Comparator.comparingInt(Fact::getStartdatum);
    private final Map<Long, List<Fact>> factsPerPatientInAisle;

    ExtendedSjukfallCalculator(List<Fact> aisle) {
        this.factsPerPatientInAisle = getFactsPerPatient(aisle);
    }

    /**
     * Will extend the startdate of the sjukfall as far back as possible using all intyg for the patient.
     * @param patient The patient to look for when searching for more earlier intyg.
     * @param sjukfall The sjukfall that will be extended if any suitable intygs are found.
     * @return The final sjukfall, extended with earlier facts if possible.
     */
    SjukfallExtended getExtendedSjukfallStart(long patient, @Nonnull SjukfallExtended sjukfall) {
        final List<Fact> allIntygForPatient = factsPerPatientInAisle.get(patient);
        if (allIntygForPatient == null) {
            return sjukfall;
        }
        return getExtendedSjukfallStart(sjukfall, allIntygForPatient);
    }

    private Map<Long, List<Fact>> getFactsPerPatient(List<Fact> facts) {
        return facts.stream().collect(Collectors.groupingBy(Fact::getPatient));
    }

    private SjukfallExtended getExtendedSjukfallStart(final SjukfallExtended sjukfall, Collection<Fact> allIntygForPatient) {
        final List<Fact> allEarlierIntygForPatient = allIntygForPatient.stream()
                .filter(fact -> fact.getStartdatum() < sjukfall.getStart())
                .collect(Collectors.toList());
        final Optional<Fact> extendableIntyg = allEarlierIntygForPatient.stream()
                .filter(fact -> isConnectable(sjukfall, fact))
                .min(FACT_ORDER_BY_STARTDATE);
        if (!extendableIntyg.isPresent()) {
            return sjukfall;
        }
        final SjukfallExtended mergedSjukfall = sjukfall.extendSjukfallWithNewStart(extendableIntyg.get());
        return getExtendedSjukfallStart(mergedSjukfall, allEarlierIntygForPatient);
    }

    private boolean isConnectable(SjukfallExtended sjukfall, Fact fact) {
        return fact.getStartdatum() + fact.getSjukskrivningslangd() + Sjukfall.MAX_GAP >= sjukfall.getStart();
    }

}
