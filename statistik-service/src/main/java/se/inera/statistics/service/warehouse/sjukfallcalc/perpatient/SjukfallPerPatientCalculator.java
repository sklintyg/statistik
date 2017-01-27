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
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SjukfallPerPatientCalculator {

    private final boolean useOriginalSjukfallStart;
    private final List<Range> ranges;
    private List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod;
    private final FactsToSjukfallConverter factsToSjukfallConverter;

    public SjukfallPerPatientCalculator(boolean useOriginalSjukfallStart, List<Range> ranges, Iterable<Fact> filteredAisle) {
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
        this.ranges = ranges;
        this.factsPerPatientAndPeriod = FactsPerPatientAndPeriodGrouper.group(filteredAisle, this.ranges);
        this.factsToSjukfallConverter = new FactsToSjukfallConverter();
    }

    public Multimap<Long, SjukfallExtended> getSjukfallsPerPatient(int period) {
        final ArrayListMultimap<Long, SjukfallExtended> currentSjukfallsPerPatient = getSjukfallPerPatientInPeriod(period);
        if (useOriginalSjukfallStart) {
            extendWithEarlierStart(period, currentSjukfallsPerPatient);
        }
        return currentSjukfallsPerPatient;
    }

    private void extendWithEarlierStart(int period, ArrayListMultimap<Long, SjukfallExtended> currentSjukfallsPerPatient) {
        Collection<Long> allPatiensWithPossibleEarierStart = getAllPatientWithPossibleEarlierStart(ranges.get(period), currentSjukfallsPerPatient);
        final ArrayListMultimap<Long, SjukfallExtended> allSjukfallsForEarlyPatient = getAllSjukfallsForPatients(period, allPatiensWithPossibleEarierStart);
        for (Map.Entry<Long, Collection<SjukfallExtended>> allSjukfallPerPatient : allSjukfallsForEarlyPatient.asMap().entrySet()) {
            final List<SjukfallExtended> currentSjukfallsForPatient = currentSjukfallsPerPatient.get(allSjukfallPerPatient.getKey());
            final Collection<SjukfallExtended> allSjukfallsForPatient = allSjukfallPerPatient.getValue();
            final ArrayList<SjukfallExtended> sjukfallsExtendedWithEarlierPeriods = getSjukfallExtendedWithEarlierPeriods(currentSjukfallsForPatient, allSjukfallsForPatient);
            currentSjukfallsPerPatient.replaceValues(allSjukfallPerPatient.getKey(), sjukfallsExtendedWithEarlierPeriods);
        }
    }

    @NotNull
    private ArrayList<SjukfallExtended> getSjukfallExtendedWithEarlierPeriods(List<SjukfallExtended> currentSjukfalls, Collection<SjukfallExtended> allSjukfalls) {
        final ArrayList<SjukfallExtended> sjukfallsExtendedWithEarlierPeriods = new ArrayList<>();
        for (SjukfallExtended currentSjukfall : currentSjukfalls) {
            Optional<SjukfallExtended> matching = getMatchingSjukfall(allSjukfalls, currentSjukfall);
            sjukfallsExtendedWithEarlierPeriods.add(matching.isPresent()
                    ? currentSjukfall.extendSjukfallWithPeriods(matching.get())
                    : currentSjukfall);
        }
        return sjukfallsExtendedWithEarlierPeriods;
    }

    /**
     * Tries to find a sjukfall from a list (allSjukfalls) that contains all Facts from another Sjukfall (currentSjukfall).
     *
     * @param allSjukfalls Sjukfalls where probably one item "contains" the sjukfall in the currentSjukfall param
     * @param currentSjukfall Base sjukfall to search for
     * @return The sjukfall from param allSjukfalls that "contains" the same Facts as currentSjukfall param. Empty result if not found.
     */
    private Optional<SjukfallExtended> getMatchingSjukfall(Collection<SjukfallExtended> allSjukfalls, SjukfallExtended currentSjukfall) {
        for (SjukfallExtended potentiallyLongerSjukfall : allSjukfalls) {
            if (potentiallyLongerSjukfall.containsAllIntygIn(currentSjukfall)) {
                return Optional.of(potentiallyLongerSjukfall);
            }
        }
        return Optional.empty();
    }

    private ArrayListMultimap<Long, SjukfallExtended> getAllSjukfallsForPatients(int period, Collection<Long> patientToCalculate) {
        final ArrayListMultimap<Long, Fact> factsPerPatient = ArrayListMultimap.create();
        for (int i = 0; i <= (period + 1); i++) {
            factsPerPatient.putAll(factsPerPatientAndPeriod.get(i));
        }
        final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = ArrayListMultimap.create();
        final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
        for (Long patientId : factsPerPatient.keySet()) {
            final boolean shouldCalculateForThisPatient = patientToCalculate.contains(patientId);
            if (shouldCalculateForThisPatient && (period == 0 || !factsPerPatientInPeriod.get(patientId).isEmpty())) {
                sjukfalls.putAll(factsToSjukfallConverter.getSjukfallsPerPatient(factsPerPatient.get(patientId)));
            }
        }
        return sjukfalls;
    }

    private Collection<Long> getAllPatientWithPossibleEarlierStart(Range range, ArrayListMultimap<Long, SjukfallExtended> sjukfalls) {
        final int latestStartDate = WidelineConverter.toDay(range.getFrom()) + SjukfallExtended.MAX_GAP;
        return sjukfalls.asMap().entrySet().stream()
                .filter(longCollectionEntry -> longCollectionEntry.getValue().stream()
                        .anyMatch(sjukfallExtended -> sjukfallExtended.getStart() <= latestStartDate))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private ArrayListMultimap<Long, SjukfallExtended> getSjukfallPerPatientInPeriod(int period) {
        final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = ArrayListMultimap.create();
        final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
        for (Long patientId : factsPerPatientInPeriod.keySet()) {
            sjukfalls.putAll(factsToSjukfallConverter.getSjukfallsPerPatient(factsPerPatientInPeriod.get(patientId)));
        }
        return sjukfalls;
    }

}
