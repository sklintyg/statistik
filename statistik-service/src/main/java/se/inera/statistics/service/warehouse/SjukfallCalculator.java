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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class SjukfallCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallCalculator.class);
    public static final int MAX_CACHE_SIZE = 10000;

    private final Predicate<Fact> filter;
    private final List<Fact> aisle;
    private final boolean useOriginalSjukfallStart;
    private final boolean extendSjukfall;
    private ArrayListMultimap<Integer, Sjukfall> sjukfallsPerPatientInAisle;
    private LoadingCache<Integer, List<Fact>> allIntygForPatientInAisle = null;

    private LoadingCache<Integer, List<Fact>> getAllIntygForPatientInAisle() {
        if (allIntygForPatientInAisle == null) {
            allIntygForPatientInAisle = CacheBuilder.newBuilder()
                    .maximumSize(MAX_CACHE_SIZE)
                    .build(new CacheLoader<Integer, List<Fact>>() {
                        public List<Fact> load(Integer key) {
                            return getAllIntygForPatientInAisle(key);
                        }
                    });
        }
        return allIntygForPatientInAisle;
    }

    /**
     *
     * @param aisle aisle
     * @param filter filter
     * @param useOriginalSjukfallStart true = använd faktiskt startdatum, inte första datum på första intyget som är tillgängligt för anroparen
     * @param extendSjukfall true = försök att komplettera sjukfall från andra enheter än de man har tillgång till, false = titta bara på tillgängliga enheter,
     *                       lämplig att använda t ex om man vet att man har tillgång till alla enheter
     */
    public SjukfallCalculator(List<Fact> aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart, boolean extendSjukfall) {
        this.filter = filter;
        this.aisle = new ArrayList<>(aisle);
        this.extendSjukfall = extendSjukfall;
        Collections.sort(this.aisle, Fact.TIME_ORDER);
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
    }

    private void extendSjukfallConnectedByIntygOnOtherEnhets(Multimap<Integer, Sjukfall> sjukfallForAvailableEnhets, LocalDate from) {
        final Set<Integer> patients = new HashSet<>(sjukfallForAvailableEnhets.keySet());
        final ArrayListMultimap<Integer, Sjukfall> sjukfallsPerPatient = getSjukfallsPerPatientInAisle(patients);
        for (int patient : patients) {
            final Collection<Sjukfall> sjukfalls = sjukfallForAvailableEnhets.get(patient);
                Collection<Sjukfall> sjukfallFromAllIntygForPatient = sjukfallsPerPatient.get(patient);
            if (countIntyg(sjukfalls) != countIntyg(sjukfallFromAllIntygForPatient)) {
                for (Sjukfall sjukfall : sjukfallFromAllIntygForPatient) {
                    List<Sjukfall> mergableSjukfalls = filterSjukfallInPeriod(sjukfall.getStart(), sjukfall.getEnd(), sjukfalls);
                    Sjukfall mergedSjukfall = mergeAllSjukfallInList(mergableSjukfalls);
                    if (mergedSjukfall != null) {
                        if (useOriginalSjukfallStart) {
                            mergedSjukfall = getExtendedSjukfallStart(mergedSjukfall, patient);
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

    private ArrayListMultimap<Integer, Sjukfall> getSjukfallsPerPatientInAisle(Set<Integer> patients) {
        if (sjukfallsPerPatientInAisle == null) {
            sjukfallsPerPatientInAisle = getSjukfallsPerPatient(aisle, patients);
            return sjukfallsPerPatientInAisle;
        }
        final Set<Integer> cachedPatients = sjukfallsPerPatientInAisle.keySet();
        final HashSet<Integer> nonCachedPatients = new HashSet<>(patients);
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

    Collection<Sjukfall> getSjukfalls(LocalDate from, LocalDate to) {
        final Iterable<Fact> filteredFacts = getFilteredFactsToDate(filter, to);
        Multimap<Integer, Sjukfall> sjukfallsPerPatient = getSjukfallsPerPatient(filteredFacts);
        if (extendSjukfall) {
            extendSjukfallConnectedByIntygOnOtherEnhets(sjukfallsPerPatient, from);
        }
        Multimap<Integer, Sjukfall> result = filterPersonifiedSjukfallsFromDate(from, sjukfallsPerPatient);
        return result.values();
    }

    private Iterable<Fact> getFilteredFactsToDate(final Predicate<Fact> filter, LocalDate to) {
        final int cutoff = WidelineConverter.toDay(to);
        return Iterables.filter(aisle, new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return fact.getStartdatum() < cutoff && filter.apply(fact);
            }
        });
    }

    private Multimap<Integer, Sjukfall> filterPersonifiedSjukfallsFromDate(LocalDate from, Multimap<Integer, Sjukfall> sjukfallsPerPatient) {
        final int firstday = WidelineConverter.toDay(from);
        Multimap<Integer, Sjukfall> result = ArrayListMultimap.create();
        for (Integer patient : sjukfallsPerPatient.keySet()) {
            final Collection<Sjukfall> sjukfalls = sjukfallsPerPatient.get(patient);
            for (Sjukfall sjukfall : sjukfalls) {
                if (sjukfall.getEnd() >= firstday) {
                    result.put(patient, sjukfall);
                }
            }
        }
        return result;
    }

    private ArrayListMultimap<Integer, Sjukfall> getSjukfallsPerPatient(Iterable<Fact> facts) {
        return getSjukfallsPerPatient(facts, null);
    }

    private ArrayListMultimap<Integer, Sjukfall> getSjukfallsPerPatient(Iterable<Fact> facts, Collection<Integer> patientsFilter) {
        final ArrayListMultimap<Integer, Sjukfall> sjukfallsPerPatient = ArrayListMultimap.create();
        for (Fact line : facts) {
            int key = line.getPatient();
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

    private Sjukfall getExtendedSjukfallStart(final Sjukfall mergedSjukfall, int patient) {
        List<Fact> allIntygForPatient;
        try {
            allIntygForPatient = getAllIntygForPatientInAisle().get(patient);
        } catch (ExecutionException e) {
            LOG.error("Failed to get intygs on aisle for patient: " + patient);
            allIntygForPatient = Collections.emptyList();
        }
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

        return getExtendedSjukfallStart(sjukfall, patient);
    }

    private Sjukfall mergeAllSjukfallInList(List<Sjukfall> sjukfalls) {
        if (sjukfalls == null || sjukfalls.isEmpty()) {
            return null;
        }
        sortByEndDate(sjukfalls);
        Sjukfall sjukfall = sjukfalls.get(0);
        if (sjukfalls.size() == 1) {
            return sjukfall;
        }
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
