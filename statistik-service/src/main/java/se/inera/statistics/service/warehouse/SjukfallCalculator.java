/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SjukfallCalculator {

    private final Predicate<Fact> filter;
    private final List<Fact> aisle = new ArrayList<>();
    private boolean useOriginalSjukfallStart = false;

    public SjukfallCalculator(List<Fact> aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart) {
        this.filter = filter;
        this.aisle.addAll(aisle);
        Collections.sort(this.aisle, Fact.TIME_ORDER);
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
    }

    private void extendSjukfallConnectedByIntygOnOtherEnhets(List<PersonifiedSjukfall> sjukfallForAvailableEnhets, boolean useOriginalSjukfallStart, LocalDate from, LocalDate to) {
        Multimap<Integer, Sjukfall> sjukfallsWithSamePatient = findSjukfallWithSamePatient(sjukfallForAvailableEnhets, useOriginalSjukfallStart);
        for (int patient : sjukfallsWithSamePatient.keySet()) {
            connectIfPossible(patient, sjukfallsWithSamePatient.get(patient), sjukfallForAvailableEnhets, useOriginalSjukfallStart, from, to);
        }
    }

    List<PersonifiedSjukfall> getSjukfalls(LocalDate from, LocalDate to) {
        List<PersonifiedSjukfall> result = getSjukfallForAvailableEnhets(from, to);
        extendSjukfallConnectedByIntygOnOtherEnhets(result, useOriginalSjukfallStart, from, to);
        return result;
    }

    private List<PersonifiedSjukfall> getSjukfallForAvailableEnhets(LocalDate from, LocalDate to) {
        final Map<Integer, PersonifiedSjukfall> active = new HashMap<>();
        final Collection<PersonifiedSjukfall> sjukfalls = new ArrayList<>();
        int cutoff = WidelineConverter.toDay(to);
        for (Fact line : aisle) {
            if (line.getStartdatum() >= cutoff) {
                break;
            }
            if (filter.apply(line)) {
                process(line, sjukfalls, active);
            }
        }
        List<PersonifiedSjukfall> result = new ArrayList<>();
        int firstday = WidelineConverter.toDay(from);
        for (PersonifiedSjukfall sjukfall : sjukfalls) {
            if (sjukfall.getSjukfall().getEnd() >= firstday) {
                result.add(sjukfall);
            }
        }

        for (PersonifiedSjukfall sjukfall : active.values()) {
            if (sjukfall.getSjukfall().getEnd() >= firstday) {
                result.add(sjukfall);
            }
        }
        return result;
    }

    private void connectIfPossible(int patient, Collection<Sjukfall> sjukfallsForPatientOnAvailableEnhets, List<PersonifiedSjukfall> sjukfallsForAvailableEnhets, boolean useOriginalSjukfallStart, LocalDate from, LocalDate to) {
        List<Fact> allIntygForPatient = getAllIntygForPatientInAisle(patient);
        List<Sjukfall> sjukfallFromAllIntygForPatient = calculateSjukfallForIntyg(allIntygForPatient, from, to);
        for (Sjukfall sjukfall : sjukfallFromAllIntygForPatient) {
            List<Sjukfall> mergableSjukfalls = filterSjukfallInPeriod(sjukfall.getStart(), sjukfall.getEnd(), sjukfallsForPatientOnAvailableEnhets);
            Sjukfall mergedSjukfall = mergeAllSjukfallInList(mergableSjukfalls);
            if (mergedSjukfall != null) {
                if (useOriginalSjukfallStart) {
                    mergedSjukfall = getExtendedSjukfallStart(mergedSjukfall, allIntygForPatient);
                }
                for (Sjukfall sjukfall1 : mergableSjukfalls) {
                    sjukfallsForAvailableEnhets.remove(new PersonifiedSjukfall(sjukfall1, patient));
                }
                sjukfallsForAvailableEnhets.add(new PersonifiedSjukfall(mergedSjukfall, patient));
            }
        }
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

    private List<Sjukfall> calculateSjukfallForIntyg(List<Fact> intygs, LocalDate from, LocalDate to) {
        final SjukfallCalculator sjukfallCalculator = new SjukfallCalculator(intygs, new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return true;
            }
        }, useOriginalSjukfallStart);
        final List<PersonifiedSjukfall> sjukfalls = sjukfallCalculator.getSjukfallForAvailableEnhets(from, to);
        return Lists.transform(sjukfalls, new Function<PersonifiedSjukfall, Sjukfall>() {
            @Override
            public Sjukfall apply(PersonifiedSjukfall personifiedSjukfall) {
                return personifiedSjukfall.getSjukfall();
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

    private Multimap<Integer, Sjukfall> findSjukfallWithSamePatient(List<PersonifiedSjukfall> sjukfalls, boolean includePatientWithSingleSjukfall) {
        final Multimap<Integer, Sjukfall> sjukfallPerPatient = ArrayListMultimap.create();
        for (PersonifiedSjukfall personifiedSjukfall : sjukfalls) {
            sjukfallPerPatient.put(personifiedSjukfall.getPatient(), personifiedSjukfall.getSjukfall());
        }
        if (includePatientWithSingleSjukfall) {
            return sjukfallPerPatient;
        }
        final Set<Integer> patients = sjukfallPerPatient.keySet();
        final Multimap<Integer, Sjukfall> patientsWithMoreThanOneSjukfall = ArrayListMultimap.create();
        for (Integer integer : patients) {
            final Collection<Sjukfall> sjukfallsForPatient = sjukfallPerPatient.get(integer);
            if (sjukfallsForPatient.size() > 1) {
                patientsWithMoreThanOneSjukfall.putAll(integer, sjukfallsForPatient);
            }
        }
        return patientsWithMoreThanOneSjukfall;
    }

    private void process(Fact line, Collection<PersonifiedSjukfall> sjukfalls, Map<Integer, PersonifiedSjukfall> active) {
        int key = line.getPatient();
        PersonifiedSjukfall sjukfall = active.get(key);

        if (sjukfall == null) {
            sjukfall = new PersonifiedSjukfall(line);
            active.put(key, sjukfall);
        } else {
            Sjukfall nextSjukfall = sjukfall.getSjukfall().join(line);
            active.put(key, new PersonifiedSjukfall(nextSjukfall, key));
            if (!nextSjukfall.isExtended()) {
                sjukfalls.add(sjukfall);
            }
        }
    }

}
