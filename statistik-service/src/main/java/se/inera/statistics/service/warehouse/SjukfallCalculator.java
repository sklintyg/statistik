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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SjukfallCalculator {

    private final Predicate<Fact> filter;
    private final List<Fact> aisle;
    private final boolean useOriginalSjukfallStart;

    public SjukfallCalculator(List<Fact> aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart) {
        this.filter = filter;
        this.aisle = new ArrayList<>(aisle);
        Collections.sort(this.aisle, Fact.TIME_ORDER);
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
    }

    private Collection<Sjukfall> extendSjukfallConnectedByIntygOnOtherEnhets(Multimap<Integer, Sjukfall> sjukfallForAvailableEnhets, LocalDate from) {
        final Collection<Sjukfall> sjukfalls = new ArrayList<>(sjukfallForAvailableEnhets.values());
        for (int patient : sjukfallForAvailableEnhets.keySet()) {
            List<Fact> allIntygForPatient = getAllIntygForPatientInAisle(patient);
            Collection<Sjukfall> sjukfallFromAllIntygForPatient = calculateSjukfallForIntyg(allIntygForPatient, from);
            for (Sjukfall sjukfall : sjukfallFromAllIntygForPatient) {
                List<Sjukfall> mergableSjukfalls = filterSjukfallInPeriod(sjukfall.getStart(), sjukfall.getEnd(), sjukfallForAvailableEnhets.get(patient));
                Sjukfall mergedSjukfall = mergeAllSjukfallInList(mergableSjukfalls);
                if (mergedSjukfall != null) {
                    if (useOriginalSjukfallStart) {
                        mergedSjukfall = getExtendedSjukfallStart(mergedSjukfall, allIntygForPatient);
                    }
                    for (Sjukfall mergableSjukfall : mergableSjukfalls) {
                        sjukfalls.remove(mergableSjukfall);
                    }
                    sjukfalls.add(mergedSjukfall);
                }
            }
        }
        return sjukfalls;
    }

    Collection<Sjukfall> getSjukfalls(LocalDate from, LocalDate to) {
        final Iterable<Fact> filteredFacts = getFilteredFactsToDate(filter, to);
        Map<Integer, Stack<Sjukfall>> sjukfallsPerPatient = getSjukfallsPerPatient(filteredFacts);
        Multimap<Integer, Sjukfall> result = filterPersonifiedSjukfallsFromDate(from, sjukfallsPerPatient);
        return extendSjukfallConnectedByIntygOnOtherEnhets(result, from);
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

    private Multimap<Integer, Sjukfall> filterPersonifiedSjukfallsFromDate(LocalDate from, Map<Integer, Stack<Sjukfall>> sjukfallsPerPatient) {
        final int firstday = WidelineConverter.toDay(from);
        Multimap<Integer, Sjukfall> result = ArrayListMultimap.create();
        for (Map.Entry<Integer, Stack<Sjukfall>> patientSjukfallEntry : sjukfallsPerPatient.entrySet()) {
            for (Sjukfall sjukfall : patientSjukfallEntry.getValue()) {
                if (sjukfall.getEnd() >= firstday) {
                    result.put(patientSjukfallEntry.getKey(), sjukfall);
                }
            }
        }
        return result;
    }

    private Map<Integer, Stack<Sjukfall>> getSjukfallsPerPatient(Iterable<Fact> facts) {
        final Map<Integer, Stack<Sjukfall>> sjukfallsPerPatient = new HashMap<>();
        for (Fact line : facts) {
            int key = line.getPatient();
            Stack<Sjukfall> sjukfallsForPatient = sjukfallsPerPatient.get(key);

            if (sjukfallsForPatient == null) {
                final Stack<Sjukfall> sjukfalls = new Stack<>();
                Sjukfall sjukfall = new Sjukfall(line);
                sjukfalls.add(sjukfall);
                sjukfallsPerPatient.put(key, sjukfalls);
            } else {
                final Sjukfall sjukfall = sjukfallsForPatient.pop();
                Sjukfall nextSjukfall = sjukfall.join(line);
                if (!nextSjukfall.isExtended()) {
                    sjukfallsForPatient.push(sjukfall);
                }
                sjukfallsForPatient.push(nextSjukfall);
            }
        }
        return sjukfallsPerPatient;
    }

    private Sjukfall getExtendedSjukfallStart(final Sjukfall mergedSjukfall, List<Fact> allIntygForPatient) {
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

    private Collection<Sjukfall> calculateSjukfallForIntyg(List<Fact> intygs, LocalDate from) {
        final Map<Integer, Stack<Sjukfall>> sjukfallsPerPatient = getSjukfallsPerPatient(intygs);
        final Multimap<Integer, Sjukfall> sjukfalls = filterPersonifiedSjukfallsFromDate(from, sjukfallsPerPatient);
        return Collections2.transform(sjukfalls.entries(), new Function<Map.Entry<Integer, Sjukfall>, Sjukfall>() {
            @Override
            public Sjukfall apply(Map.Entry<Integer, Sjukfall> integerSjukfallEntry) {
                return integerSjukfallEntry.getValue();
            }
        });
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
