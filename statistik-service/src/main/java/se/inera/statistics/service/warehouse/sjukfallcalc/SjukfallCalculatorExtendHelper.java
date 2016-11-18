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

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class SjukfallCalculatorExtendHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallCalculatorExtendHelper.class);
    private final List<Fact> aisle;
    private final boolean useOriginalSjukfallStart;
    private ArrayListMultimap<Long, Fact> factsPerPatientInAisle;
    private ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatientInAisle;

    SjukfallCalculatorExtendHelper(boolean useOriginalSjukfallStart, List<Fact> aisle) {
        this.aisle = aisle;
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
    }

    void extendSjukfallConnectedByIntygOnOtherEnhets(Multimap<Long, SjukfallExtended> sjukfallForAvailableEnhets) {
        final Set<Long> patients = new HashSet<>(sjukfallForAvailableEnhets.keySet());
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = getSjukfallsPerPatientInAisle(patients);
        for (long patient : patients) {
            extendSjukfallConnectedByIntygOnOtherEnhetsForPatientIfNeeded(sjukfallForAvailableEnhets, sjukfallsPerPatient, patient);
        }
    }

    private ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatientInAisle(Set<Long> patients) {
        if (sjukfallsPerPatientInAisle == null) {
            sjukfallsPerPatientInAisle = SjukfallCalculatorHelper.getSjukfallsPerPatient(aisle, patients);
            return sjukfallsPerPatientInAisle;
        }
        final Set<Long> cachedPatients = sjukfallsPerPatientInAisle.keySet();
        final HashSet<Long> nonCachedPatients = new HashSet<>(patients);
        nonCachedPatients.removeAll(cachedPatients);
        if (!nonCachedPatients.isEmpty()) {
            sjukfallsPerPatientInAisle.putAll(SjukfallCalculatorHelper.getSjukfallsPerPatient(aisle, nonCachedPatients));
        }
        return sjukfallsPerPatientInAisle;
    }

    private void extendSjukfallConnectedByIntygOnOtherEnhetsForPatientIfNeeded(Multimap<Long, SjukfallExtended> sjukfallForAvailableEnhets, ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient, long patient) {
        final Collection<SjukfallExtended> sjukfalls = sjukfallForAvailableEnhets.get(patient);
        Collection<SjukfallExtended> sjukfallFromAllIntygForPatient = sjukfallsPerPatient.get(patient);
        final boolean noExtraSjukfallExistsOnOtherEnhet = countIntyg(sjukfalls) == countIntyg(sjukfallFromAllIntygForPatient);
        if (noExtraSjukfallExistsOnOtherEnhet) {
            return; //All intygs for patient are already included
        }
        SjukfallExtended firstSjukfallOnAvailableEnhetsForPatient = useOriginalSjukfallStart ? getFirstSjukfall(sjukfalls) : null;
        for (SjukfallExtended sjukfallFromAllVgForPatient : sjukfallFromAllIntygForPatient) {
            mergeAndUpdateSjukfall(patient, sjukfalls, firstSjukfallOnAvailableEnhetsForPatient, sjukfallFromAllVgForPatient);
        }
    }

    private int countIntyg(Collection<SjukfallExtended> sjukfalls) {
        int counter = 0;
        for (SjukfallExtended sjukfall : sjukfalls) {
            counter += sjukfall.getIntygCount();
        }
        return counter;
    }

    private SjukfallExtended getFirstSjukfall(Collection<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null) {
            return null;
        }
        SjukfallExtended currentFirstSjukfall = null;
        for (SjukfallExtended sjukfall : sjukfalls) {
            if (currentFirstSjukfall == null || sjukfall.getStart() < currentFirstSjukfall.getStart()) {
                currentFirstSjukfall = sjukfall;
            }
        }
        return currentFirstSjukfall;
    }

    private void mergeAndUpdateSjukfall(long patient, Collection<SjukfallExtended> sjukfallsFromAvailableEnhetsForPatient, SjukfallExtended firstSjukfallOnAvailableEnhetsForPatient, SjukfallExtended sjukfallFromAllVgForPatient) {
        List<SjukfallExtended> mergableSjukfalls = getSjukfallsFromAvailableEnhetsIncludedInSjukfallFromAllVg(sjukfallsFromAvailableEnhetsForPatient, sjukfallFromAllVgForPatient);
        final SjukfallExtended mergedSjukfall = mergeAllSjukfallInList(mergableSjukfalls);
        if (mergedSjukfall == null) {
            return;
        }
        updateMergedSjukfall(patient, sjukfallsFromAvailableEnhetsForPatient, firstSjukfallOnAvailableEnhetsForPatient, sjukfallFromAllVgForPatient, mergableSjukfalls, mergedSjukfall);
    }

    private List<SjukfallExtended> getSjukfallsFromAvailableEnhetsIncludedInSjukfallFromAllVg(Collection<SjukfallExtended> sjukfallsFromAvailableEnhetsForPatient, SjukfallExtended sjukfallFromAllVgForPatient) {
        return filterSjukfallInPeriod(sjukfallFromAllVgForPatient.getStart(), sjukfallFromAllVgForPatient.getEnd(), sjukfallsFromAvailableEnhetsForPatient);
    }

    private List<SjukfallExtended> filterSjukfallInPeriod(final int start, final int end, Collection<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null || sjukfalls.isEmpty()) {
            return Collections.emptyList();
        }
        return sjukfalls.stream()
                .filter(sjukfall -> sjukfall.getEnd() >= start && sjukfall.getStart() <= end)
                .collect(Collectors.toList());
    }

    private void updateMergedSjukfall(long patient, Collection<SjukfallExtended> sjukfalls, SjukfallExtended firstSjukfall, SjukfallExtended sjukfall, List<SjukfallExtended> mergableSjukfalls, SjukfallExtended mergedSjukfall) {
        SjukfallExtended mergedSjukfallExtendedWithRealDays = mergedSjukfall.extendWithRealDaysWithinPeriod(sjukfall);
        if (mergedSjukfallExtendedWithRealDays == null) {
            LOG.error("extendWithRealDaysWithinPeriod should not return null");
            return;
        }
        if (firstSjukfall != null && firstSjukfall.getStart() == mergedSjukfallExtendedWithRealDays.getStart()) {
            mergedSjukfallExtendedWithRealDays = getExtendedSjukfallStart(patient, mergedSjukfallExtendedWithRealDays);
        }
        for (SjukfallExtended mergableSjukfall : mergableSjukfalls) {
            sjukfalls.remove(mergableSjukfall);
        }
        sjukfalls.add(mergedSjukfallExtendedWithRealDays);
    }

    private SjukfallExtended getExtendedSjukfallStart(long patient, SjukfallExtended mergedSjukfall) {
        if (factsPerPatientInAisle == null) {
            factsPerPatientInAisle = getFactsPerPatient(aisle);
        }
        return getExtendedSjukfallStart(mergedSjukfall, factsPerPatientInAisle.get(patient));
    }

    private ArrayListMultimap<Long, Fact> getFactsPerPatient(Iterable<Fact> facts) {
        ArrayListMultimap<Long, Fact> factsPerPatient = ArrayListMultimap.create();
        for (Fact fact : facts) {
            factsPerPatient.put(fact.getPatient(), fact);
        }
        return factsPerPatient;
    }

    private SjukfallExtended getExtendedSjukfallStart(final SjukfallExtended mergedSjukfall, Collection<Fact> allIntygForPatient) {
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

    private SjukfallExtended mergeAllSjukfallInList(List<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null || sjukfalls.isEmpty()) {
            return null;
        }
        if (sjukfalls.size() == 1) {
            return sjukfalls.get(0);
        }
        sortByEndDate(sjukfalls);
        SjukfallExtended sjukfall = sjukfalls.get(0);
        for (int i = 1; i < sjukfalls.size(); i++) {
            final SjukfallExtended nextSjukfall = sjukfalls.get(i);
            sjukfall = sjukfall.extendSjukfall(nextSjukfall);
        }
        return sjukfall;
    }

    private void sortByEndDate(List<SjukfallExtended> sjukfalls) {
        Collections.sort(sjukfalls, (o1, o2) -> o1.getEnd() - o2.getEnd());
    }

}
