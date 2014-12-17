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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.inera.statistics.service.report.model.Range;


public class SjukfallIterator implements Iterator<SjukfallGroup> {

    private final LocalDate from;
    private int period = 0;
    private final int periods;
    private final int periodSize;
    private final Predicate<Fact> filter;
    private final Map<Integer, PersonifiedSjukfall> active = new HashMap<>();
    private Collection<PersonifiedSjukfall> sjukfalls;
    private Fact pendingLine;
    private List<Fact> aisle;

    public SjukfallIterator(LocalDate from, int periods, int periodSize, Aisle aisle, Predicate<Fact> filter) {
        this.from = from;
        this.periods = periods;
        this.periodSize = periodSize;
        this.filter = filter;
        this.aisle = aisle.getLines();
    }

    private SjukfallIterator(LocalDate from, int periods, int periodSize, List<Fact> aisle, Predicate<Fact> filter, int period) {
        this.from = from;
        this.periods = periods;
        this.periodSize = periodSize;
        this.filter = filter;
        this.aisle = aisle;
        this.period = period;
    }

    @Override
    public boolean hasNext() {
        return period < periods;
    }

    @Override
    public SjukfallGroup next() {
        List<PersonifiedSjukfall> result = getSjukfalls();
        extendSjukfallConnectedByIntygOnOtherEnhets(result);

        Range range = new Range(from.plusMonths(period * periodSize), from.plusMonths(period * periodSize + periodSize - 1));
        SjukfallGroup sjukfallGroup = new SjukfallGroup(range, Lists.transform(result, new Function<PersonifiedSjukfall, Sjukfall>() {
            @Override
            public Sjukfall apply(PersonifiedSjukfall sjukfallExtended) {
                return sjukfallExtended.getSjukfall();
            }
        }));
        period++;
        return sjukfallGroup;
    }

    private void extendSjukfallConnectedByIntygOnOtherEnhets(List<PersonifiedSjukfall> sjukfallForAvailableEnhets) {
        Multimap<Integer, Sjukfall> sjukfallsWithSamePatient = findSjukfallWithSamePatient(sjukfallForAvailableEnhets);
        for (int patient : sjukfallsWithSamePatient.keySet()) {
            connectIfPossible(patient, sjukfallsWithSamePatient.get(patient), sjukfallForAvailableEnhets);
        }
    }

    private List<PersonifiedSjukfall> getSjukfalls() {
        sjukfalls = new ArrayList<>();
        int cutoff = WidelineConverter.toDay(from.plusMonths((period + 1) * periodSize));
        if (pendingLine != null && pendingLine.getStartdatum() < cutoff) {
            process(pendingLine);
            pendingLine = null;
        }
        if (pendingLine == null) {
            for (Fact line : aisle) {
                if (line.getStartdatum() >= cutoff) {
                    pendingLine = line;
                    break;
                }
                process(line);
            }
        }
        List<PersonifiedSjukfall> result = new ArrayList<>();
        int firstday = WidelineConverter.toDay(from.plusMonths(period * periodSize));
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

    private void connectIfPossible(int patient, Collection<Sjukfall> sjukfallsForPatientOnAvalableEnhets, List<PersonifiedSjukfall> sjukfallsForAvailableEnhets) {
        List<Fact> allIntygForPatient = getAllIntygForPatientInAisle(patient);
        List<Sjukfall> sjukfallFromAllIntygForPatient = calculateSjukfallForIntyg(allIntygForPatient);
        for (Sjukfall sjukfall : sjukfallFromAllIntygForPatient) {
            List<Sjukfall> mergableSjukfalls = filterSjukfallInPeriod(sjukfall.getStart(), sjukfall.getEnd(), sjukfallsForPatientOnAvalableEnhets);
            Sjukfall mergedSjukfall = mergeAllSjukfallInList(mergableSjukfalls);
            if (mergedSjukfall != null) {
                for (Sjukfall sjukfall1 : mergableSjukfalls) {
                    sjukfallsForAvailableEnhets.remove(new PersonifiedSjukfall(sjukfall1, patient));
                }
                sjukfallsForAvailableEnhets.add(new PersonifiedSjukfall(mergedSjukfall, patient));
            }
        }
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

    private List<Sjukfall> calculateSjukfallForIntyg(List<Fact> intygs) {
        final SjukfallIterator sjukfallIterator = new SjukfallIterator(from, periods, periodSize, intygs, new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return true;
            }
        }, period);
        final List<PersonifiedSjukfall> sjukfalls = sjukfallIterator.getSjukfalls();
        return Lists.transform(sjukfalls, new Function<PersonifiedSjukfall, Sjukfall>() {
            @Override
            public Sjukfall apply(PersonifiedSjukfall personifiedSjukfall) {
                return personifiedSjukfall.getSjukfall();
            }
        });
    }

    private List<Fact> getAllIntygForPatientInAisle(final int patient) {
        final Collection<Fact> alIntygForPAtientInAisle = Collections2.filter(aisle, new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return fact.getPatient() == patient;
            }
        });
        return new ArrayList<>(alIntygForPAtientInAisle);
    }

    private Multimap<Integer, Sjukfall> findSjukfallWithSamePatient(List<PersonifiedSjukfall> sjukfalls) {
        final Multimap<Integer, Sjukfall> sjukfallPerPatient = ArrayListMultimap.create();
        for (PersonifiedSjukfall personifiedSjukfall : sjukfalls) {
            sjukfallPerPatient.put(personifiedSjukfall.getPatient(), personifiedSjukfall.getSjukfall());
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

    private void process(Fact line) {
        if (!filter.apply(line)) {
            return;
        }

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

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
