/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SjukfallCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallCalculator.class);

    private final List<Fact> aisle;
    private final boolean useOriginalSjukfallStart;
    private final boolean extendSjukfall; //true = försök att komplettera sjukfall från andra enheter än de man har tillgång till, false = titta bara på tillgängliga enheter, lämplig att använda t ex om man vet att man har tillgång till alla enheter
    private final List<Range> ranges;
    private ArrayListMultimap<Long, Sjukfall> sjukfallsPerPatientInAisle;
    private List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod;
    private int period = 0;
    private Multimap<Long, Sjukfall> sjukfallsPerPatientInPreviousPeriod = ArrayListMultimap.create();
    private ArrayListMultimap<Long, Fact> factsPerPatientInAisle;

    /**
     *
     * @param aisle aisle
     * @param filter filter
     * @param useOriginalSjukfallStart true = använd faktiskt startdatum, inte första datum på första intyget som är tillgängligt för anroparen
     */
    public SjukfallCalculator(List<Fact> aisle, Predicate<Fact> filter, List<Range> ranges, boolean useOriginalSjukfallStart) {
        this.aisle = new ArrayList<>(aisle);
        this.extendSjukfall = !SjukfallUtil.ALL_ENHETER.getFilter().equals(filter);
        Collections.sort(this.aisle, Fact.TIME_ORDER);
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
        rangeEnds.add(WidelineConverter.toDay(ranges.get(0).getFrom()));
        for (Range range : ranges) {
            rangeEnds.add(WidelineConverter.toDay(range.getTo()));
        }

        List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = new ArrayList<>(rangeEnds.size());
        for (int i = 0; i < rangeEnds.size(); i++) {
            factsPerPatientAndPeriod.add(ArrayListMultimap.<Long, Fact> create());
        }

        for (Fact fact : facts) {
            final int rangeIndex = getRangeIndex(fact.getStartdatum(), rangeEnds);
            if (rangeIndex >= 0) {
                factsPerPatientAndPeriod.get(rangeIndex).put(fact.getPatient(), fact);
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

    private static int getRangeIndex(int date, List<Integer> rangeEnds) {
        final int rangesSize = rangeEnds.size();
        for (int i = 0; i < rangesSize; i++) {
            final Integer rangeEnd = rangeEnds.get(i);
            if (date < rangeEnd) {
                return i;
            }
        }
        return -1;
    }

    private void extendSjukfallConnectedByIntygOnOtherEnhets(Multimap<Long, Sjukfall> sjukfallForAvailableEnhets) {
        final Set<Long> patients = new HashSet<>(sjukfallForAvailableEnhets.keySet());
        final ArrayListMultimap<Long, Sjukfall> sjukfallsPerPatient = getSjukfallsPerPatientInAisle(patients);
        for (long patient : patients) {
            final Collection<Sjukfall> sjukfalls = sjukfallForAvailableEnhets.get(patient);
            Collection<Sjukfall> sjukfallFromAllIntygForPatient = sjukfallsPerPatient.get(patient);
            if (countIntyg(sjukfalls) != countIntyg(sjukfallFromAllIntygForPatient)) {
                Sjukfall firstSjukfall = useOriginalSjukfallStart ? getFirstSjukfall(sjukfalls) : null;
                for (Sjukfall sjukfall : sjukfallFromAllIntygForPatient) {
                    List<Sjukfall> mergableSjukfalls = filterSjukfallInPeriod(sjukfall.getStart(), sjukfall.getEnd(), sjukfalls);
                    Sjukfall mergedSjukfall = mergeAllSjukfallInList(mergableSjukfalls);
                    if (mergedSjukfall != null) {
                        if (useOriginalSjukfallStart && firstSjukfall != null && firstSjukfall.getStart() == mergedSjukfall.getStart()) {
                            mergedSjukfall = getExtendedSjukfallStart(patient, mergedSjukfall);
                        }
                        for (Sjukfall mergableSjukfall : mergableSjukfalls) {
                            sjukfalls.remove(mergableSjukfall);
                        }
                        sjukfalls.add(mergedSjukfall);
                    }
                }
            }
        }
    }

    private Sjukfall getExtendedSjukfallStart(long patient, Sjukfall mergedSjukfall) {
        if (factsPerPatientInAisle == null) {
            factsPerPatientInAisle = getFactsPerPatient(aisle);
        }
        return getExtendedSjukfallStart(mergedSjukfall, factsPerPatientInAisle.get(patient));
    }

    private Sjukfall getFirstSjukfall(Collection<Sjukfall> sjukfalls) {
        if (sjukfalls == null) {
            return null;
        }
        Sjukfall currentFirstSjukfall = null;
        for (Sjukfall sjukfall : sjukfalls) {
            if (currentFirstSjukfall == null || sjukfall.getStart() < currentFirstSjukfall.getStart()) {
                currentFirstSjukfall = sjukfall;
            }
        }
        return currentFirstSjukfall;
    }

    private ArrayListMultimap<Long, Sjukfall> getSjukfallsPerPatientInAisle(Set<Long> patients) {
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

    private int countIntyg(Collection<Sjukfall> sjukfalls) {
        int counter = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            counter += sjukfall.getIntygCount();
        }
        return counter;
    }

    Collection<Sjukfall> getSjukfallsForNextPeriod() {
        Multimap<Long, Sjukfall> sjukfallsPerPatient = getSjukfallsPerPatient();
        if (extendSjukfall) {
            extendSjukfallConnectedByIntygOnOtherEnhets(sjukfallsPerPatient);
        }
        Multimap<Long, Sjukfall> result = filterPersonifiedSjukfallsFromDate(ranges.get(period).getFrom(), sjukfallsPerPatient);
        period++;
        return result.values();
    }

    private Multimap<Long, Sjukfall> getSjukfallsPerPatient() {
        final ArrayListMultimap<Long, Fact> result = ArrayListMultimap.create();
        for (int i = 0; i <= (period + 1); i++) {
            result.putAll(factsPerPatientAndPeriod.get(i));
        }
        final ArrayListMultimap<Long, Sjukfall> sjukfalls = ArrayListMultimap.create(sjukfallsPerPatientInPreviousPeriod);
        final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
        for (Long key : result.keySet()) {
            if (period == 0 || !factsPerPatientInPeriod.get(key).isEmpty()) {
                sjukfalls.removeAll(key);
                sjukfalls.putAll(getSjukfallsPerPatient(result.get(key)));
            }
        }
        sjukfallsPerPatientInPreviousPeriod = ArrayListMultimap.create(sjukfalls);
        return sjukfalls;
    }

    private Multimap<Long, Sjukfall> filterPersonifiedSjukfallsFromDate(LocalDate from, Multimap<Long, Sjukfall> sjukfallsPerPatient) {
        final int firstday = WidelineConverter.toDay(from);
        Multimap<Long, Sjukfall> result = ArrayListMultimap.create();
        for (Long patient : sjukfallsPerPatient.keySet()) {
            final Collection<Sjukfall> sjukfalls = sjukfallsPerPatient.get(patient);
            for (Sjukfall sjukfall : sjukfalls) {
                if (sjukfall.getEnd() >= firstday) {
                    result.put(patient, sjukfall);
                }
            }
        }
        return result;
    }

    private ArrayListMultimap<Long, Sjukfall> getSjukfallsPerPatient(Iterable<Fact> facts) {
        return getSjukfallsPerPatient(facts, null);
    }

    private ArrayListMultimap<Long, Sjukfall> getSjukfallsPerPatient(Iterable<Fact> facts, Collection<Long> patientsFilter) {
        final ArrayListMultimap<Long, Sjukfall> sjukfallsPerPatient = ArrayListMultimap.create();
        for (Fact line : facts) {
            long key = line.getPatient();
            if (patientsFilter != null && !patientsFilter.contains(key)) {
                continue;
            }
            List<Sjukfall> sjukfallsForPatient = sjukfallsPerPatient.get(key);

            if (sjukfallsForPatient.isEmpty()) {
                Sjukfall sjukfall = new Sjukfall(line);
                sjukfallsPerPatient.put(key, sjukfall);
            } else {
                final Sjukfall sjukfall = sjukfallsForPatient.remove(sjukfallsForPatient.size() - 1);
                Sjukfall nextSjukfall = sjukfall.join(line);
                if (!nextSjukfall.isExtended()) {
                    sjukfallsPerPatient.put(key, sjukfall);
                }
                sjukfallsPerPatient.put(key, nextSjukfall);
            }
        }
        return sjukfallsPerPatient;
    }

    private Sjukfall getExtendedSjukfallStart(final Sjukfall mergedSjukfall, Collection<Fact> allIntygForPatient) {
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
        final Sjukfall sjukfall = mergedSjukfall.extendSjukfallWithNewStart(startdatum, sjukskrivningslangd);

        return getExtendedSjukfallStart(sjukfall, allIntygForPatient);
    }

    private Sjukfall mergeAllSjukfallInList(List<Sjukfall> sjukfalls) {
        if (sjukfalls == null || sjukfalls.isEmpty()) {
            return null;
        }
        if (sjukfalls.size() == 1) {
            return sjukfalls.get(0);
        }
        sortByEndDate(sjukfalls);
        Sjukfall sjukfall = sjukfalls.get(0);
        for (int i = 1; i < sjukfalls.size(); i++) {
            final Sjukfall nextSjukfall = sjukfalls.get(i);
            sjukfall = sjukfall.extendSjukfall(nextSjukfall);
        }
        return sjukfall;
    }

    private void sortByEndDate(List<Sjukfall> sjukfalls) {
        Collections.sort(sjukfalls, new Comparator<Sjukfall>() {
            @Override
            public int compare(Sjukfall o1, Sjukfall o2) {
                return o1.getEnd() - o2.getEnd();
            }
        });
    }

    private List<Sjukfall> filterSjukfallInPeriod(final int start, final int end, Collection<Sjukfall> sjukfalls) {
        final Collection<Sjukfall> filteredSjukfalls = Collections2.filter(sjukfalls, new Predicate<Sjukfall>() {
            @Override
            public boolean apply(Sjukfall sjukfall) {
                return sjukfall.getEnd() > start && sjukfall.getStart() < end;
            }
        });
        return new ArrayList<>(filteredSjukfalls);
    }

    private List<Fact> getAllIntygForPatientInAisle(final int patient) {
        final Collection<Fact> allIntygForPatientInAisle = Collections2.filter(aisle, new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return fact.getPatient() == patient;
            }
        });
        return new ArrayList<>(allIntygForPatientInAisle);
    }

}
