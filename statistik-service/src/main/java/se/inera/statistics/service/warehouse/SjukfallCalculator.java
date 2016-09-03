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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import se.inera.statistics.service.report.model.Range;

public class SjukfallCalculator {

    private final List<Fact> aisle;
    private final boolean useOriginalSjukfallStart;
    private final boolean extendSjukfall; //true = försök att komplettera sjukfall från andra enheter än de man har tillgång till, false = titta bara på tillgängliga enheter, lämplig att använda t ex om man vet att man har tillgång till alla enheter
    private final List<Range> ranges;
    private ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatientInAisle;
    private List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod;
    private int period = 0;
    private Multimap<Long, SjukfallExtended> sjukfallsPerPatientInPreviousPeriod = ArrayListMultimap.create();
    private ArrayListMultimap<Long, Fact> factsPerPatientInAisle;

    /**
     * @param useOriginalSjukfallStart true = använd faktiskt startdatum, inte första datum på första intyget som är tillgängligt för anroparen
     */
    public SjukfallCalculator(Aisle aisle, Predicate<Fact> filter, List<Range> ranges, boolean useOriginalSjukfallStart) {
        this.aisle = new ArrayList<>(aisle.getLines());
        this.extendSjukfall = !SjukfallUtil.ALL_ENHETER.getIntygFilter().equals(filter);
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
        final Iterable<Fact> filteredAisle = Iterables.filter(aisle, filter);
        this.ranges = new ArrayList<>(ranges);
        populateFactsPerPatientAndPeriod(filteredAisle, this.ranges);
    }

    private void populateFactsPerPatientAndPeriod(Iterable<Fact> facts, List<Range> ranges) {
        factsPerPatientAndPeriod = getFactsPerPatientAndPeriod(facts, ranges, useOriginalSjukfallStart);
    }

    static List<ArrayListMultimap<Long, Fact>> getFactsPerPatientAndPeriod(Iterable<Fact> facts, List<Range> ranges, boolean useOriginalSjukfallStart) {
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

        if (useOriginalSjukfallStart) {
            for (Fact fact : facts) {
                final int rangeIndex = getRangeIndex(fact.getStartdatum(), rangeEnds);
                if (rangeIndex >= 0) {
                    factsPerPatientAndPeriod.get(rangeIndex).put(fact.getPatient(), fact);
                }
            }
        } else {
            for (Fact fact : facts) {
                final List<Integer> rangeIndexes = getRangeIndexes(fact.getStartdatum(), fact.getSlutdatum(), rangeEnds);
                for (Integer rangeIndex : rangeIndexes) {
                    if (rangeIndex >= 0) {
                        factsPerPatientAndPeriod.get(rangeIndex).put(fact.getPatient(), fact);
                    }
                }
            }
        }
        return factsPerPatientAndPeriod;
    }

    private ArrayListMultimap<Long, Fact> getFactsPerPatient(Iterable<Fact> facts) {
        ArrayListMultimap<Long, Fact> factsPerPatient = ArrayListMultimap.create();
        for (Fact fact : facts) {
            factsPerPatient.put(fact.getPatient(), fact);
        }
        return factsPerPatient;
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

    private void extendSjukfallConnectedByIntygOnOtherEnhets(Multimap<Long, SjukfallExtended> sjukfallForAvailableEnhets) {
        final Set<Long> patients = new HashSet<>(sjukfallForAvailableEnhets.keySet());
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = getSjukfallsPerPatientInAisle(patients);
        for (long patient : patients) {
            final Collection<SjukfallExtended> sjukfalls = sjukfallForAvailableEnhets.get(patient);
            Collection<SjukfallExtended> sjukfallFromAllIntygForPatient = sjukfallsPerPatient.get(patient);
            if (countIntyg(sjukfalls) != countIntyg(sjukfallFromAllIntygForPatient)) {
                SjukfallExtended firstSjukfall = useOriginalSjukfallStart ? getFirstSjukfall(sjukfalls) : null;
                for (SjukfallExtended sjukfall : sjukfallFromAllIntygForPatient) {
                    List<SjukfallExtended> mergableSjukfalls = filterSjukfallInPeriod(sjukfall.getStart(), sjukfall.getEnd(), sjukfalls);
                    final SjukfallExtended mergedSjukfall = mergeAllSjukfallInList(mergableSjukfalls);
                    if (mergedSjukfall != null) {
                        SjukfallExtended mergedSjukfallExtendedWithRealDays = mergedSjukfall.extendWithRealDaysWithinPeriod(sjukfall);
                        if (mergedSjukfallExtendedWithRealDays != null) {
                            if (useOriginalSjukfallStart && firstSjukfall != null && firstSjukfall.getStart() == mergedSjukfallExtendedWithRealDays.getStart()) {
                                mergedSjukfallExtendedWithRealDays = getExtendedSjukfallStart(patient, mergedSjukfallExtendedWithRealDays);
                            }
                            for (SjukfallExtended mergableSjukfall : mergableSjukfalls) {
                                sjukfalls.remove(mergableSjukfall);
                            }
                            sjukfalls.add(mergedSjukfallExtendedWithRealDays);
                        }
                    }
                }
            }
        }
    }

    private SjukfallExtended getExtendedSjukfallStart(long patient, SjukfallExtended mergedSjukfall) {
        if (factsPerPatientInAisle == null) {
            factsPerPatientInAisle = getFactsPerPatient(aisle);
        }
        return getExtendedSjukfallStart(mergedSjukfall, factsPerPatientInAisle.get(patient));
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

    private ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatientInAisle(Set<Long> patients) {
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

    private int countIntyg(Collection<SjukfallExtended> sjukfalls) {
        int counter = 0;
        for (SjukfallExtended sjukfall : sjukfalls) {
            counter += sjukfall.getIntygCount();
        }
        return counter;
    }

    Collection<Sjukfall> getSjukfallsForNextPeriod() {
        Multimap<Long, SjukfallExtended> sjukfallsPerPatient = getSjukfallsPerPatient();
        if (extendSjukfall) {
            extendSjukfallConnectedByIntygOnOtherEnhets(sjukfallsPerPatient);
        }
        Multimap<Long, SjukfallExtended> result = filterPersonifiedSjukfallsFromDate(ranges.get(period).getFrom(), sjukfallsPerPatient);
        period++;
        return Collections2.transform(result.values(), new Function<SjukfallExtended, Sjukfall>() {
            @Override
            public Sjukfall apply(SjukfallExtended sjukfallExtended) {
                return Sjukfall.create(sjukfallExtended);
            }
        });
    }

    private Multimap<Long, SjukfallExtended> getSjukfallsPerPatient() {
        if (useOriginalSjukfallStart) {
            final ArrayListMultimap<Long, Fact> result = ArrayListMultimap.create();
            for (int i = 0; i <= (period + 1); i++) {
                result.putAll(factsPerPatientAndPeriod.get(i));
            }
            final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = ArrayListMultimap.create(sjukfallsPerPatientInPreviousPeriod);
            final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
            for (Long key : result.keySet()) {
                if (period == 0 || !factsPerPatientInPeriod.get(key).isEmpty()) {
                    sjukfalls.removeAll(key);
                    sjukfalls.putAll(getSjukfallsPerPatient(result.get(key)));
                }
            }
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
        final int startdatum = intygForExtending.getStartdatum();
        final int sjukskrivningslangd = intygForExtending.getSjukskrivningslangd();
        final SjukfallExtended sjukfall = mergedSjukfall.extendSjukfallWithNewStart(startdatum, sjukskrivningslangd);

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
        Collections.sort(sjukfalls, new Comparator<SjukfallExtended>() {
            @Override
            public int compare(SjukfallExtended o1, SjukfallExtended o2) {
                return o1.getEnd() - o2.getEnd();
            }
        });
    }

    private List<SjukfallExtended> filterSjukfallInPeriod(final int start, final int end, Collection<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null || sjukfalls.isEmpty()) {
            return Collections.emptyList();
        }
        final Collection<SjukfallExtended> filteredSjukfalls = Collections2.filter(sjukfalls, new Predicate<SjukfallExtended>() {
            @Override
            public boolean apply(SjukfallExtended sjukfall) {
                return sjukfall.getEnd() > start && sjukfall.getStart() < end;
            }
        });
        return new ArrayList<>(filteredSjukfalls);
    }

}
