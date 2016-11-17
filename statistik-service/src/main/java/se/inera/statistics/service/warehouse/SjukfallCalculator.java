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
package se.inera.statistics.service.warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.Range;

class SjukfallCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallCalculator.class);
    private final List<Fact> aisle;
    private final boolean useOriginalSjukfallStart;
    private final boolean extendSjukfall; //true = försök att komplettera sjukfall från andra enheter än de man har tillgång till, false = titta bara på tillgängliga enheter, lämplig att använda t ex om man vet att man har tillgång till alla enheter
    private final List<Range> ranges;
    private List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod;
    private int period = 0;
    private Multimap<Long, SjukfallExtended> sjukfallsPerPatientInPreviousPeriod = ArrayListMultimap.create();
    private SjukfallCalculatorExtendHelper sjukfallCalculatorExtendHelper;

    /**
     * @param useOriginalSjukfallStart true = använd faktiskt startdatum, inte första datum på första intyget som är tillgängligt för anroparen
     */
    SjukfallCalculator(Aisle aisle, Predicate<Fact> filter, List<Range> ranges, boolean useOriginalSjukfallStart) {
        this.aisle = new ArrayList<>(aisle.getLines());
        this.extendSjukfall = !SjukfallUtil.ALL_ENHETER.getIntygFilter().equals(filter);
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
        final Iterable<Fact> filteredAisle = Iterables.filter(aisle, filter);
        this.ranges = new ArrayList<>(ranges);
        populateFactsPerPatientAndPeriod(filteredAisle, this.ranges);
    }

    private void populateFactsPerPatientAndPeriod(Iterable<Fact> facts, List<Range> ranges) {
        factsPerPatientAndPeriod = getFactsPerPatientAndPeriod(facts, ranges);
    }

    static List<ArrayListMultimap<Long, Fact>> getFactsPerPatientAndPeriod(Iterable<Fact> facts, List<Range> ranges) {
        final List<Integer> rangeEnds = new ArrayList<>(ranges.size() + 1);
        rangeEnds.add(WidelineConverter.toDay(ranges.get(0).getFrom().minusDays(1)));
        for (Range range : ranges) {
            rangeEnds.add(WidelineConverter.toDay(range.getTo()));
        }
        rangeEnds.add(Integer.MAX_VALUE);

        List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = new ArrayList<>(rangeEnds.size());
        for (int i = 0; i < rangeEnds.size(); i++) {
            factsPerPatientAndPeriod.add(ArrayListMultimap.<Long, Fact> create());
        }

        populateFactsPerPatientAndPeriod(facts, rangeEnds, factsPerPatientAndPeriod);
        return factsPerPatientAndPeriod;
    }

    private static void populateFactsPerPatientAndPeriod(Iterable<Fact> facts, List<Integer> rangeEnds, List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod) {
        for (Fact fact : facts) {
            final List<Integer> rangeIndexes = getRangeIndexes(fact.getStartdatum(), fact.getSlutdatum(), rangeEnds);
            for (Integer rangeIndex : rangeIndexes) {
                if (rangeIndex >= 0) {
                    factsPerPatientAndPeriod.get(rangeIndex).put(fact.getPatient(), fact);
                }
            }
        }
    }

    private static void populateFactsPerPatientAndPeriodUsingOriginalSjukfallStart(Iterable<Fact> facts, List<Integer> rangeEnds, List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod) {
        for (Fact fact : facts) {
            final int rangeIndex = getRangeIndex(fact.getStartdatum(), rangeEnds);
            if (rangeIndex >= 0) {
                factsPerPatientAndPeriod.get(rangeIndex).put(fact.getPatient(), fact);
            }
        }
    }

    private static List<Integer> getRangeIndexes(int startdatum, int slutdatum, List<Integer> rangeEnds) {
        final int startIndex = getRangeIndex(startdatum, rangeEnds);
        final int slutIndex = getRangeIndex(slutdatum, rangeEnds);
        final ArrayList<Integer> indexes = new ArrayList<>();
        if (startIndex >= 0 && slutIndex >= 0) {
            for (int i = startIndex; i <= slutIndex; i++) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private static int getRangeIndex(int date, List<Integer> rangeEnds) {
        final int rangesSize = rangeEnds.size();
        for (int i = 0; i < rangesSize; i++) {
            final Integer rangeEnd = rangeEnds.get(i);
            if (date <= rangeEnd) {
                return i;
            }
        }
        return -1;
    }

    Collection<Sjukfall> getSjukfallsForNextPeriod() {
        Multimap<Long, SjukfallExtended> sjukfallsPerPatient = getSjukfallsPerPatient();
        if (extendSjukfall) {
            if (sjukfallCalculatorExtendHelper == null) {
                sjukfallCalculatorExtendHelper = new SjukfallCalculatorExtendHelper(this.useOriginalSjukfallStart, this.aisle);
            }
            sjukfallCalculatorExtendHelper.extendSjukfallConnectedByIntygOnOtherEnhets(sjukfallsPerPatient);
        }
        Multimap<Long, SjukfallExtended> result = filterPersonifiedSjukfallsFromDate(ranges.get(period).getFrom(), sjukfallsPerPatient);
        period++;
        return result.values().stream().map(Sjukfall::create).collect(Collectors.toList());
    }

    private Multimap<Long, SjukfallExtended> getSjukfallsPerPatient() {
        if (useOriginalSjukfallStart) {
            final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = ArrayListMultimap.create();
            final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
            for (Long patientId : factsPerPatientInPeriod.keySet()) {
                sjukfalls.putAll(getSjukfallsPerPatient(factsPerPatientInPeriod.get(patientId)));
            }
            extendWithEarlierStart(period, sjukfalls);
            sjukfallsPerPatientInPreviousPeriod = ArrayListMultimap.create(sjukfalls);
            return sjukfalls;
        } else {
            final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = ArrayListMultimap.create();
            final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
            for (Long patientId : factsPerPatientInPeriod.keySet()) {
                sjukfalls.putAll(getSjukfallsPerPatient(factsPerPatientInPeriod.get(patientId)));
            }
            return sjukfalls;
        }
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
                sjukfalls.putAll(getSjukfallsPerPatient(factsPerPatient.get(patientId)));
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
            sjukfalls.putAll(getSjukfallsPerPatient(factsPerPatientInPeriod.get(patientId)));
        }
        return sjukfalls;
    }


    private Multimap<Long, SjukfallExtended> filterPersonifiedSjukfallsFromDate(LocalDate from, Multimap<Long, SjukfallExtended> sjukfallsPerPatient) {
        final int firstday = WidelineConverter.toDay(from);
        Multimap<Long, SjukfallExtended> result = ArrayListMultimap.create();
        for (Long patient : sjukfallsPerPatient.keySet()) {
            final Collection<SjukfallExtended> sjukfalls = sjukfallsPerPatient.get(patient);
            for (SjukfallExtended sjukfall : sjukfalls) {
                if (sjukfall.getEnd() >= firstday) {
                    result.put(patient, sjukfall);
                }
            }
        }
        return result;
    }

    private ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Iterable<Fact> facts) {
        return SjukfallCalculatorExtendHelper.getSjukfallsPerPatient(facts, null);
    }

}
